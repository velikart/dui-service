package ru.axenix.smartax.dui.service.client.configuration;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.mock.env.MockEnvironment;
import ru.axenix.smartax.dui.service.client.api.TestApi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DuiServiceApiRegistrarTest {

    private TestableDuiServiceApiRegistrar registrar;
    private SimpleBeanDefinitionRegistry registry;

    @BeforeEach
    void setUp() {
        registrar = new TestableDuiServiceApiRegistrar();
        registry = new SimpleBeanDefinitionRegistry();

        registrar.setEnvironment(new MockEnvironment()
                .withProperty("OPENAPI_API_PACKAGE", "ru.axenix.smartax.dui.service.client.api"));
        registrar.setResourceLoader(new DefaultResourceLoader());
    }

    @Test
    void registerBeanDefinitionsShouldRegisterApiClassesWithRequiredConstructor() {
        BeanDefinition beanDefinition = BeanDefinitionBuilder
                .genericBeanDefinition(TestApi.class)
                .getBeanDefinition();
        registrar.setComponents(Set.of(beanDefinition));

        registrar.registerBeanDefinitions(
                AnnotationMetadata.introspect(DuiServiceApiRegistrar.class),
                registry
        );

        assertTrue(registry.containsBeanDefinition("testApi"));
        assertEquals(
                TestApi.class.getName(),
                registry.getBeanDefinition("testApi").getBeanClassName()
        );
    }

    @Test
    void registerBeanDefinitionsShouldIgnoreBeanDefinitionsWithoutClassName() {
        BeanDefinition beanDefinition = BeanDefinitionBuilder
                .genericBeanDefinition()
                .getBeanDefinition();
        registrar.setComponents(Set.of(beanDefinition));

        registrar.registerBeanDefinitions(
                AnnotationMetadata.introspect(DuiServiceApiRegistrar.class),
                registry
        );

        assertFalse(registry.containsBeanDefinition("testApi"));
    }

    @Test
    void registerBeanDefinitionsShouldIgnoreUnknownClassName() {
        BeanDefinition beanDefinition = BeanDefinitionBuilder
                .genericBeanDefinition("ru.axenix.smartax.dui.service.client.api.UnknownApi")
                .getBeanDefinition();
        registrar.setComponents(Set.of(beanDefinition));

        registrar.registerBeanDefinitions(
                AnnotationMetadata.introspect(DuiServiceApiRegistrar.class),
                registry
        );

        assertFalse(registry.containsBeanDefinition("unknownApi"));
    }

    @Test
    void findComponentsShouldReturnNonNullSet() {
        TestableDuiServiceApiRegistrar realRegistrar = new TestableDuiServiceApiRegistrar();
        realRegistrar.setEnvironment(new MockEnvironment()
                .withProperty("OPENAPI_API_PACKAGE", "ru.axenix.smartax.dui.service.client.api"));
        realRegistrar.setResourceLoader(new DefaultResourceLoader());

        Set<BeanDefinition> components = realRegistrar.callRealFindComponents();

        assertNotNull(components);
    }

    private static final class TestableDuiServiceApiRegistrar extends DuiServiceApiRegistrar {

        private Set<BeanDefinition> components;

        void setComponents(Set<BeanDefinition> components) {
            this.components = components;
        }

        @Override
        protected Set<BeanDefinition> findComponents() {
            if (components != null) {
                return components;
            }
            return super.findComponents();
        }

        Set<BeanDefinition> callRealFindComponents() {
            return super.findComponents();
        }
    }
}