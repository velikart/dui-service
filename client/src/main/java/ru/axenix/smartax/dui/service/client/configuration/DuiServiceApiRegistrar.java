package ru.axenix.smartax.dui.service.client.configuration;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.stereotype.Component;
import ru.axenix.smartax.dui.service.client.invoker.ApiClient;

@Component
@ConditionalOnClass(ApiClient.class)
public class DuiServiceApiRegistrar
        implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(DuiServiceApiRegistrar.class);
    private static final String API_BASE_PACKAGE = "ru.axenix.smartax.dui.service.client.api";

    private ResourceLoader resourceLoader;
    private Environment environment;

    @Override
    public void registerBeanDefinitions(
            AnnotationMetadata metadata,
            BeanDefinitionRegistry registry
    ) {
        Set<BeanDefinition> components = findComponents();

        for (BeanDefinition beanDefinition : components) {
            String className = beanDefinition.getBeanClassName();

            if (className != null) {
                registerApiBean(className, registry);
            }
        }
    }

    private void registerApiBean(String className, BeanDefinitionRegistry registry) {
        try {
            Class<?> apiClass = Class.forName(className);
            BeanDefinitionBuilder builder = BeanDefinitionBuilder
                    .genericBeanDefinition(apiClass)
                    .addConstructorArgReference("duiApiClient");
            String beanName = lowerFirst(apiClass.getSimpleName());

            Optional.ofNullable(builder.getBeanDefinition())
                    .ifPresentOrElse(
                            beanDefinition -> registerNonNullBeanDefinition(registry, beanName, beanDefinition),
                            () -> LOG.warn("BeanDefinition is null for API client class {}", className)
                    );
        } catch (ClassNotFoundException ex) {
            LOG.warn("Failed to register API client bean for class {}", className, ex);
        }
    }

    private void registerNonNullBeanDefinition(
            BeanDefinitionRegistry registry,
            String beanName,
            BeanDefinition beanDefinition
    ) {
        registry.registerBeanDefinition(beanName, beanDefinition);
    }

    protected Set<BeanDefinition> findComponents() {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false, environment);
        scanner.addIncludeFilter((TypeFilter) (metadataReader, metadataReaderFactory) -> {
            try {
                Class<?> clazz = Class.forName(metadataReader.getClassMetadata().getClassName());
                return hasRequiredConstructor(clazz);
            } catch (ClassNotFoundException ex) {
                LOG.warn(
                        "Failed to load API client class {}",
                        metadataReader.getClassMetadata().getClassName(),
                        ex
                );
                return false;
            }
        });
        scanner.setResourceLoader(resourceLoader);
        return scanner.findCandidateComponents(API_BASE_PACKAGE);
    }

    private static boolean hasRequiredConstructor(Class<?> clazz) {
        return Arrays.stream(clazz.getConstructors())
                .anyMatch(constructor ->
                        constructor.getParameterCount() == 1
                                && ApiClient.class.isAssignableFrom(constructor.getParameterTypes()[0])
                );
    }

    private static String lowerFirst(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}