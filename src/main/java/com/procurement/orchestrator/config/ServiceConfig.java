package com.procurement.orchestrator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan(basePackages = "com.procurement.orchestrator.service")
public class ServiceConfig {
    @Bean
    public RestTemplate getRestClient() {
        RestTemplate restClient = new RestTemplate(
            new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        return restClient;
    }
}
