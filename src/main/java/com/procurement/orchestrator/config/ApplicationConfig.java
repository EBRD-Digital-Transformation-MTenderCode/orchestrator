package com.procurement.orchestrator.config;

import com.fasterxml.jackson.core.JsonGenerator;
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
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

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
    public JsonUtil jsonUtil(final ObjectMapper objectMapper) {
        objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        return new JsonUtil(objectMapper);
    }

//    @Bean
//    @Primary
//    public Jackson2ObjectMapperBuilder jacksonBuilder() {
//        final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
////        builder.featuresToEnable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
//        builder.featuresToEnable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
//        builder.featuresToEnable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
//        return builder;
//    }

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
//    public Jackson2ObjectMapperBuilder builder(final Jackson2ObjectMapperBuilder builder){
//        return builder;
//    }

//    @Bean
//    @Primary
//    public Jackson2ObjectMapperBuilder objectMapperBuilder() {
//        final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
//        builder.deserializerByType(DecimalNode.class, new MoneyDeserializer());
//        builder.featuresToEnable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
//        return builder;
//    }

//    @Bean
//    @Primary
//    public ObjectMapper objectMapper(final ObjectMapper objectMapper) {
////        mapper.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true);
//        objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
//        objectMapper.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
//        return objectMapper;
//    }

}
