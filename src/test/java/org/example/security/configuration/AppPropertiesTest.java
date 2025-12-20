package org.example.security.configuration;

import org.example.api.config.AppProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class AppPropertiesTest {

    @Autowired
    private AppProperties appProperties;

    @Test
    void testPropertiesLoaded() {
        assertNotNull(appProperties);
        assertEquals("spring-core-epam", appProperties.getName());
        assertEquals(3600000L, appProperties.getSecurity().getJwtExpiration());
    }
}
