package com.procurement.orchestrator.config.properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cassandra.datasource")
public class CassandraProperties {

    private String contactPoint;

    private String keyspace;

    private String username;

    private String password;

}
