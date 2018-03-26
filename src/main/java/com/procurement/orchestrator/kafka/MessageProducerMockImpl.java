package com.procurement.orchestrator.kafka;

import com.procurement.orchestrator.domain.PlatformMessage;
import com.procurement.orchestrator.domain.chronograph.ChronographTask;
import org.springframework.stereotype.Service;

@Service
public class MessageProducerMockImpl implements MessageProducer {

    public boolean sendToChronograph(ChronographTask task) {
        return true;
    }

    public boolean sendToPlatform(PlatformMessage message) {
        return true;
    }
}
