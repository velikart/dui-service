package ru.axenix.smartax.dui.service.configuration;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Конфигурация работы по JPA.
 */
@Configuration
@ConditionalOnClass(DataSource.class)
@EntityScan(basePackages = {
        "ru.axenix.smartax.dui.service"
})
@EnableJpaRepositories(basePackages = {
        "ru.axenix.smartax.dui.service"
})
public class JpaConfiguration {
}
