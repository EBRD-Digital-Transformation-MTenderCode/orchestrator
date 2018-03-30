package com.procurement.orchestrator.config.kafka;

import com.procurement.orchestrator.domain.PlatformMessage;
import com.procurement.orchestrator.domain.chronograph.ChronographTask;

public class MessageProducerMockImpl implements MessageProducer {

    public boolean sendToChronograph(final ChronographTask task) {
        return true;
    }

    public boolean sendToPlatform(final PlatformMessage message) {
        return true;
    }
}
