package com.procurement.orchestrator.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "kafka.producer")
public class KafkaProducerProperties {
    private String internalBootstrap;
    private String externalBootstrap;
}
