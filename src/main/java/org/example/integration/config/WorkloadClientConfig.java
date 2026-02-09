package org.example.integration.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class WorkloadClientConfig {

    @Bean
    public DownstreamHeadersInterceptor downstreamHeadersInterceptor() {
        return new DownstreamHeadersInterceptor();
    }

    // Apache HttpClient (engine) with pooling + timeouts (tune if you want)
    @Bean
    public CloseableHttpClient apacheHttpClient() {
        var requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(3))     // connection establish timeout
                .setResponseTimeout(Timeout.ofSeconds(10))   // read/response timeout
                .build();

        var connManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnTotal(200)
                .setMaxConnPerRoute(50)
                .build();

        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connManager)
                .build();
    }

    // Load-balanced RestClient builder that uses Apache HttpClient underneath
    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder(
            CloseableHttpClient apacheHttpClient,
            DownstreamHeadersInterceptor interceptor
    ) {
        var requestFactory = new HttpComponentsClientHttpRequestFactory(apacheHttpClient);

        return RestClient.builder()
                .requestFactory(requestFactory)
                .requestInterceptor(interceptor);
    }

    @Bean
    public RestClient workloadRestClient(RestClient.Builder loadBalancedRestClientBuilder) {
        return loadBalancedRestClientBuilder
                .baseUrl("http://workload-service")
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
