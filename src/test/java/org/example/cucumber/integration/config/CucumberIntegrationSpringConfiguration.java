package org.example.cucumber.integration.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(
        classes = CucumberIntegrationSpringConfiguration.IntegrationTestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:trainingdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
                "spring.jpa.properties.hibernate.jdbc.time_zone=UTC",
                "spring.activemq.broker-url=vm://localhost?broker.persistent=false",
                "spring.activemq.user=admin",
                "spring.activemq.password=admin",
                "spring.jms.listener.auto-startup=false",
                "eureka.client.enabled=false",
                "eureka.client.register-with-eureka=false",
                "eureka.client.fetch-registry=false",
                "app.security.jwt-secret=mySecretKeyThatIsAtLeast256BitsLongForHS256Algorithm123456",
                "spring.cloud.client.hostname=localhost",
                "spring.cloud.client.ip-address=127.0.0.1"
        }
)
@ActiveProfiles("component-test")
public class CucumberIntegrationSpringConfiguration {

    @TestConfiguration
    @EnableAutoConfiguration
    @ComponentScan(
            basePackages = "org.example",
            excludeFilters = @ComponentScan.Filter(
                    type = FilterType.REGEX,
                    pattern = "org\\.example\\.api\\.controller\\.LoginController"
            )
    )
    static class IntegrationTestApp {
    }
}


