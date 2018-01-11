package com.procurement.orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableFeignClients
//@EnableEurekaClient
public class OrchestratorApplication {

    public static void main(final String[] args) {
        SpringApplication.run(OrchestratorApplication.class, args);
    }
}
