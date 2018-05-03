package com.procurement.orchestrator.config.kafka;

import com.procurement.orchestrator.domain.PlatformMessage;
import com.procurement.orchestrator.domain.chronograph.ScheduleTask;

public interface MessageProducer {

    boolean sendToChronograph(ScheduleTask task);

    boolean sendToPlatform(PlatformMessage message);

}

