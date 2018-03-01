package com.procurement.orchestrator.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@EnableConfigurationProperties(KafkaProducerProperties.class)
public class KafkaProducerConfig {

//    private static final String SASL_JAAS_TEMPLATE = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";";
//    private static final String SASL_USERNAME = "YodaAdmin";
//    private static final String SASL_PASSWORD = "XgsZrPA.UF/8[.Kvy;ej&v\\qZa_NdM]c";
//    private static final String SASL_JAAS = String.format(SASL_JAAS_TEMPLATE, SASL_USERNAME, SASL_PASSWORD);
//    private final KafkaProducerProperties kafkaProperties;
//
//    public KafkaProducerConfig(final KafkaProducerProperties kafkaProperties) {
//        this.kafkaProperties = kafkaProperties;
//    }
//
//    private Map<String, Object> internalProducerConfigs() {
//        Map<String, Object> internalProps = new HashMap<>();
//        internalProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getInternalBootstrap());
//        internalProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        internalProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        return internalProps;
//    }
//
//    private Map<String, Object> externalProducerConfigs() {
//        Map<String, Object> externalProps = new HashMap<>();
//        externalProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getExternalBootstrap());
//        externalProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        externalProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        externalProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
//        externalProps.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
//        externalProps.put(SaslConfigs.SASL_JAAS_CONFIG, SASL_JAAS);
//        return externalProps;
//    }
//
//    @Bean(name = "internalKafkaTemplate")
//    public KafkaTemplate<String, String> internalKafkaTemplate() {
//        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(internalProducerConfigs()));
//    }
//
//    @Bean(name = "externalKafkaTemplate")
//    public KafkaTemplate<String, String> externalKafkaTemplate() {
//        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(externalProducerConfigs()));
//    }
}
