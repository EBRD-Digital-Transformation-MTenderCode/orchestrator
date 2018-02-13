package com.procurement.orchestrator.config;

import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(10000, 10000);
    }

}
