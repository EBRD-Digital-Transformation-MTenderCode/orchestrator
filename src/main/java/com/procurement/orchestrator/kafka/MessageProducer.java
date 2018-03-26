package com.procurement.orchestrator.kafka;

import com.procurement.orchestrator.domain.PlatformMessage;
import com.procurement.orchestrator.domain.chronograph.ChronographTask;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public interface MessageProducer {

    boolean sendToChronograph(ChronographTask task);

    boolean sendToPlatform(PlatformMessage message);

}

