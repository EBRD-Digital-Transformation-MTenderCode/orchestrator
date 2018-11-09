package com.procurement.orchestrator.config.kafka;

import com.procurement.orchestrator.delegate.kafka.MessageProducer;
import com.procurement.orchestrator.delegate.kafka.MessageProducerMockImpl;
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
