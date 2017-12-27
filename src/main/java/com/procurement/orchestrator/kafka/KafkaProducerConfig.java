package com.procurement.orchestrator.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(KafkaConsumerProperties.class)
public class KafkaProducerConfig {

    private final KafkaProducerProperties kafkaProducerProperties;

    public KafkaProducerConfig(final KafkaProducerProperties kafkaProducerProperties) {
        this.kafkaProducerProperties = kafkaProducerProperties;
    }

    @Bean
    public ProducerFactory<String, Task> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs(), stringKeySerializer(), messageJsonSerializer());
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProducerProperties.getBootstrap());
        return props;
    }

    @Bean
    public KafkaTemplate<String, Task> messageKafkaTemplate() {
        KafkaTemplate<String, Task> kafkaTemplate = new KafkaTemplate<>(producerFactory());
        kafkaTemplate.setDefaultTopic(kafkaProducerProperties.getTopic());
        return kafkaTemplate;
    }

    @Bean
    public Serializer stringKeySerializer() {
        return new StringSerializer();
    }

    @Bean
    public Serializer messageJsonSerializer() {
        return new JsonSerializer();
    }
}
