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
//    @Bean
//    @Primary
//    public ObjectMapper objectMapper() {
//        final ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
//        mapper.configure(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN, true);
//        return mapper;
//    }

}
