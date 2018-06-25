package com.procurement.orchestrator.config.kafka;

import com.procurement.orchestrator.utils.JsonUtil;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Profile("default")
@Configuration
@EnableConfigurationProperties(KafkaProducerProperties.class)
public class KafkaProducerConfig {

    private final KafkaProducerProperties kafkaProperties;

    public KafkaProducerConfig(final KafkaProducerProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    private Map<String, Object> internalProducerConfigs() {
        final Map<String, Object> internalProps = new HashMap<>();
        internalProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getInternalBootstrap());
        internalProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        internalProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return internalProps;
    }

    @Bean
    public KafkaTemplate<String, String> internalKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(internalProducerConfigs()));
    }

    @Bean
    public MessageProducer messageProducer(final JsonUtil jsonUtil) {
        return new MessageProducerImpl(internalKafkaTemplate(), jsonUtil);
    }
}
