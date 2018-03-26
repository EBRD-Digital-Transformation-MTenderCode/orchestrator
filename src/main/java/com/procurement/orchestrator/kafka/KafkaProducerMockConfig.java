package com.procurement.orchestrator.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class KafkaProducerMockConfig {

    @Bean
    public MessageProducer messageProducer() {
        return new MessageProducerMockImpl();
    }

}
