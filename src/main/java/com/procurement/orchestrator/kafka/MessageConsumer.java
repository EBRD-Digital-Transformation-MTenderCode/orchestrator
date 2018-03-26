package com.procurement.orchestrator.kafka;

import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public interface MessageConsumer {

    void onReceiving(String message,
                     Integer offset,
                     int partition,
                     String topic,
                     Acknowledgment acknowledgment);
}

