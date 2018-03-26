package com.procurement.orchestrator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.procurement.orchestrator.config.kafka.KafkaConsumerConfig;
import com.procurement.orchestrator.config.kafka.KafkaProducerConfig;
import com.procurement.orchestrator.config.kafka.KafkaProducerMockConfig;
import com.procurement.orchestrator.utils.JsonUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        WebConfig.class,
        ServiceConfig.class,
        DaoConfiguration.class,
        KafkaConsumerConfig.class,
        KafkaProducerConfig.class,
        KafkaProducerMockConfig.class
})
public class ApplicationConfig {
    @Bean
    public JsonUtil jsonUtil(final ObjectMapper mapper) {
        return new JsonUtil(mapper);
    }
}
