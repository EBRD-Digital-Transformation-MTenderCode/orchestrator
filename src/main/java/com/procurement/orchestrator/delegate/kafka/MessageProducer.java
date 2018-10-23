package com.procurement.orchestrator.delegate.kafka;

import com.procurement.orchestrator.domain.Notification;
import com.procurement.orchestrator.domain.chronograph.ScheduleTask;
import com.procurement.orchestrator.domain.dto.CommandMessage;

public interface MessageProducer {

    boolean sendToChronograph(ScheduleTask task);

    boolean sendToPlatform(Notification notification);

    boolean sendToAuction(CommandMessage commandMessage);

}

