package com.procurement.orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BpeApplication {

    public static void main(final String[] args) {
        SpringApplication.run(BpeApplication.class, args);
    }
}
