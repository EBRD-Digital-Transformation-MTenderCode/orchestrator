package com.procurement.orchestrator.config.kafka;

import com.procurement.orchestrator.domain.PlatformMessage;
import com.procurement.orchestrator.domain.chronograph.ChronographTask;

public interface MessageProducer {

    boolean sendToChronograph(ChronographTask task);

    boolean sendToPlatform(PlatformMessage message);

}

