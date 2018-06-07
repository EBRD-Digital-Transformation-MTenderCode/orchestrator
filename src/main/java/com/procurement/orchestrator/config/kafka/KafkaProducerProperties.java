package com.procurement.orchestrator.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "kafka.producer")
public class KafkaProducerProperties {
    private String internalBootstrap;
}
