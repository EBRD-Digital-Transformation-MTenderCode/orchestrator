package com.procurement.orchestrator.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.procurement.orchestrator.service")
public class ServiceConfig {
}
