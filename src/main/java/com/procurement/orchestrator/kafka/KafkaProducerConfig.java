package com.procurement.orchestrator.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@EnableConfigurationProperties(KafkaProducerProperties.class)
public class KafkaProducerConfig {

    private final KafkaProducerProperties kafkaProperties;

    public KafkaProducerConfig(final KafkaProducerProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public Map<String, Object> internalProducerConfigs() {
        Map<String, Object> internalProps = new HashMap<>();
        internalProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getInternalBootstrap());
        internalProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        internalProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return internalProps;
    }

    @Bean
    public Map<String, Object> externalProducerConfigs() {
        Map<String, Object> externalProps = new HashMap<>();
        externalProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getExternalBootstrap());
        externalProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        externalProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return externalProps;
    }

    @Bean(name = "internalKafkaTemplate")
    public KafkaTemplate<String, String> internalKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(internalProducerConfigs()));
    }

    @Bean(name = "externalKafkaTemplate")
    public KafkaTemplate<String, String> externalKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(externalProducerConfigs()));
    }
}
