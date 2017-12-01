package com.procurement.orchestrator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.procurement.orchestrator.utils.JsonUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    WebConfig.class,
    ServiceConfig.class,
    CassandraConfig.class,
    ConverterConfig.class
})
public class ApplicationConfig {
    @Bean
    public JsonUtil jsonUtil(final ObjectMapper mapper) {
        return new JsonUtil(mapper);
    }
}
