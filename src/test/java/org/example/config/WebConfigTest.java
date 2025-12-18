package org.example.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class WebConfigTest {

    @Autowired
    private WebConfig webConfig;

    @Autowired
    private MDCInterceptor mdcInterceptor;

    @Test
    void testWebConfigLoads() {
        assertThat(webConfig).isNotNull();
        assertThat(mdcInterceptor).isNotNull();
    }
}
