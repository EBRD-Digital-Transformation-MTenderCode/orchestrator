package com.procurement.orchestrator.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.procurement.orchestrator.config.kafka.KafkaConsumerConfig;
import com.procurement.orchestrator.config.kafka.KafkaProducerConfig;
import com.procurement.orchestrator.config.kafka.KafkaProducerMockConfig;
import com.procurement.orchestrator.utils.JsonUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

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
    public JsonUtil jsonUtil() {
        return new JsonUtil(objectMapper());
    }

//    @Bean
//    public JsonUtil jsonUtil() {
//        return new JsonUtil(objectMapper());
//    }
//
//    @Bean
//    @Primary
//    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
//        return new MappingJackson2HttpMessageConverter(objectMapper());
//    }
//
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true);
        mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        return mapper;
    }

}
