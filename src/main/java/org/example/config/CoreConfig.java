package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@ComponentScan(
        basePackages = "org.example",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "org\\.example\\.config\\.Web.*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "org\\.example\\.config\\.Spring.*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "org\\.example\\.config\\..*Interceptor"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "org\\.example\\.controller\\..*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "org\\.springdoc\\..*")
        }
)
@EnableJpaRepositories("org.example.repository")
@EnableTransactionManagement
public class CoreConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfig() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }
}