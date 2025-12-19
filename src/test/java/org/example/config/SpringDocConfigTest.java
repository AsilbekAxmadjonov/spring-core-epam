package org.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ImportAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
})
class SpringDocConfigTest {

    @Autowired
    private OpenAPI openAPI;

    @Autowired
    private GroupedOpenApi groupedOpenApi;

    @Test
    void testOpenAPIBeanExists() {
        assertNotNull(openAPI);
        assertEquals("Gym Management System API", openAPI.getInfo().getTitle());
    }

    @Test
    void testGroupedOpenApiBeanExists() {
        assertNotNull(groupedOpenApi);
        assertTrue(groupedOpenApi.getPathsToMatch().contains("/**"));
    }
}
