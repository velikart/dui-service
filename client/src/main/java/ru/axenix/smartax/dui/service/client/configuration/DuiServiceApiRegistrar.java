package ru.axenix.smartax.dui.service.client.configuration;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
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

/**
 * Регистрирует сгенерированные API-клиенты как Spring-бины.
 */
@Slf4j
@Component
@ConditionalOnClass(ApiClient.class)
public class DuiServiceApiRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private ResourceLoader resourceLoader;
    private Environment environment;

    /**
     * Регистрирует найденные API-клиенты как Spring-бины.
     *
     * @param metadata метаданные импортируемой конфигурации
     * @param registry реестр определений бинов
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        Set<BeanDefinition> components = findComponents();

        for (BeanDefinition beanDefinition : components) {
            String className = beanDefinition.getBeanClassName();

            if (className != null) {
                registerApiBean(className, registry);
            }
        }
    }

    /**
     * Регистрирует конкретный API-клиент по имени класса.
     *
     * @param className полное имя класса API-клиента
     * @param registry реестр определений бинов
     */
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
                            () -> log.warn("BeanDefinition is null for API client class {}", className)
                    );
        } catch (ClassNotFoundException ex) {
            log.warn("Failed to register API client bean for class {}", className, ex);
        }
    }

    /**
     * Регистрирует подготовленное определение бина.
     *
     * @param registry реестр определений бинов
     * @param beanName имя бина
     * @param beanDefinition определение бина
     */
    private void registerNonNullBeanDefinition(BeanDefinitionRegistry registry, String beanName,
                                               BeanDefinition beanDefinition) {
        registry.registerBeanDefinition(beanName, beanDefinition);
    }

    /**
     * Ищет API-классы в пакете, заданном через {@code OPENAPI_API_PACKAGE}.
     *
     * @return множество найденных определений бинов
     */
    protected Set<BeanDefinition> findComponents() {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false, environment);
        scanner.addIncludeFilter((TypeFilter) (metadataReader, metadataReaderFactory) -> {
            try {
                Class<?> clazz = Class.forName(metadataReader.getClassMetadata().getClassName());
                return hasRequiredConstructor(clazz);
            } catch (ClassNotFoundException ex) {
                log.warn(
                        "Failed to load API client class {}",
                        metadataReader.getClassMetadata().getClassName(),
                        ex
                );
                return false;
            }
        });
        scanner.setResourceLoader(resourceLoader);
        return scanner.findCandidateComponents(environment.getRequiredProperty("OPENAPI_API_PACKAGE"));
    }

    /**
     * Проверяет наличие конструктора с единственным параметром {@link ApiClient}.
     *
     * @param clazz проверяемый класс
     * @return {@code true}, если подходящий конструктор найден
     */
    private static boolean hasRequiredConstructor(Class<?> clazz) {
        return Arrays.stream(clazz.getConstructors())
                .anyMatch(constructor ->
                        constructor.getParameterCount() == 1
                                && ApiClient.class.isAssignableFrom(constructor.getParameterTypes()[0])
                );
    }

    /**
     * Преобразует первую букву строки в нижний регистр.
     *
     * @param value исходная строка
     * @return строка с первой буквой в нижнем регистре
     */
    private static String lowerFirst(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }

    /**
     * Устанавливает загрузчик ресурсов.
     *
     * @param resourceLoader загрузчик ресурсов Spring
     */
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Устанавливает окружение со свойствами приложения.
     *
     * @param environment Spring-окружение
     */
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}