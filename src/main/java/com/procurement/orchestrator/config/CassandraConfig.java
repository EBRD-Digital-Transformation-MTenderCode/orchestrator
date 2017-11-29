package com.procurement.orchestrator.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@ComponentScan(basePackages = "com.procurement.orchestrator.cassandra")
@EnableCassandraRepositories(basePackages = "com.procurement.orchestrator.cassandra")
public class CassandraConfig {
}
