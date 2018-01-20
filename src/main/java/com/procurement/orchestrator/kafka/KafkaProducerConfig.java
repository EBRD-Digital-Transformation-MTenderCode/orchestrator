package com.procurement.orchestrator.kafka;
//
//import java.util.HashMap;
//import java.util.Map;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.Serializer;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.core.DefaultKafkaProducerFactory;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.core.ProducerFactory;
//
//@Configuration
//@EnableConfigurationProperties(KafkaConsumerProperties.class)
public class KafkaProducerConfig {
//
//    private final KafkaProducerProperties kafkaProducerProperties;
//
//    public KafkaProducerConfig(final KafkaProducerProperties kafkaProducerProperties) {
//        this.kafkaProducerProperties = kafkaProducerProperties;
//    }
//
//    @Bean
//    public ProducerFactory<String, String> producerFactory() {
//        return new DefaultKafkaProducerFactory<>(producerConfigs(), stringKeySerializer(), stringJsonSerializer());
//    }
//
//    @Bean
//    public Map<String, Object> producerConfigs() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProducerProperties.getBootstrap());
//        props.put(ProducerConfig.RETRIES_CONFIG, kafkaProducerProperties.getRetries());
//        return props;
//    }
//
//    @Bean(name = "taskKafkaTemplate")
//    public KafkaTemplate<String, String> taskKafkaTemplate() {
//        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory());
//        kafkaTemplate.setDefaultTopic(kafkaProducerProperties.getTopic());
//        return kafkaTemplate;
//    }
//
//    @Bean
//    public Serializer stringKeySerializer() {
//        return new StringSerializer();
//    }
//
//    @Bean
//    public Serializer stringJsonSerializer() {
//        return new StringSerializer();
//    }
}
