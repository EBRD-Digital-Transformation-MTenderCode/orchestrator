package com.procurement.orchestrator.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.procurement.orchestrator.config.kafka.KafkaConsumerConfig;
import com.procurement.orchestrator.config.kafka.KafkaProducerConfig;
import com.procurement.orchestrator.config.kafka.KafkaProducerMockConfig;
import com.procurement.orchestrator.infrastructure.configuration.LoggerConfiguration;
import com.procurement.orchestrator.infrastructure.configuration.NotificatorConfiguration;
import com.procurement.orchestrator.infrastructure.configuration.TransformConfiguration;
import com.procurement.orchestrator.infrastructure.configuration.WebClientConfiguration;
import com.procurement.orchestrator.rest.MDMClientErrorDecoder;
import com.procurement.orchestrator.utils.JsonUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(
    {
        WebConfig.class,
        ServiceConfig.class,
        DaoConfiguration.class,
        KafkaConsumerConfig.class,
        KafkaProducerConfig.class,
        KafkaProducerMockConfig.class,
        TransformConfiguration.class,
        WebClientConfiguration.class,
        LoggerConfiguration.class,
        NotificatorConfiguration.class
    }
)
public class ApplicationConfig {

    @Bean
    public JsonUtil jsonUtil(final ObjectMapper objectMapper) {
        objectMapper.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true);
        objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        objectMapper.setNodeFactory(JsonNodeFactory.withExactBigDecimals(true));
        return new JsonUtil(objectMapper);
    }

    @Bean
    public MDMClientErrorDecoder errorDecoder() {
        return new MDMClientErrorDecoder();
    }
}
