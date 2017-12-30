package com.procurement.orchestrator.kafka;


//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.common.serialization.Deserializer;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.annotation.EnableKafka;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.listener.AbstractMessageListenerContainer;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//@EnableKafka
public class KafkaConsumerConfig {
//
//    private final KafkaConsumerProperties kafkaConsumerProperties;
//
//    public KafkaConsumerConfig(final KafkaConsumerProperties kafkaConsumerProperties) {
//        this.kafkaConsumerProperties = kafkaConsumerProperties;
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConcurrency(1);
//        factory.setConsumerFactory(consumerFactory());
//        factory.getContainerProperties().setAckMode(AbstractMessageListenerContainer.AckMode.MANUAL);
//        return factory;
//    }
//
//    @Bean
//    public ConsumerFactory<String, String> consumerFactory() {
//        return new DefaultKafkaConsumerFactory<>(consumerProps(), stringKeyDeserializer(), stringValueDeserializer());
//    }
//
//    @Bean
//    public Map<String, Object> consumerProps() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConsumerProperties.getBootstrap());
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerProperties.getGroup());
//        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
////        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
////        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
//        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
//        return props;
//    }
//
//    @Bean
//    public Deserializer stringKeyDeserializer() {
//        return new StringDeserializer();
//    }
//
//    @Bean
//    public Deserializer stringValueDeserializer() {
//        return new StringDeserializer();
//    }

}
