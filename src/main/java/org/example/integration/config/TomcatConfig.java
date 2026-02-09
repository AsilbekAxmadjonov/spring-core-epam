package org.example.integration.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> disableSoLinger() {
        return factory -> factory.addConnectorCustomizers((Connector connector) -> {
            connector.setProperty("soLingerOn", "false");
            connector.setProperty("soLingerTime", "-1");
        });
    }
}

