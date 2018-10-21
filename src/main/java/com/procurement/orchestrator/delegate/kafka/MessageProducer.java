package com.procurement.orchestrator.delegate.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Notification;
import com.procurement.orchestrator.domain.chronograph.ScheduleTask;

public interface MessageProducer {

    boolean sendToChronograph(ScheduleTask task);

    boolean sendToPlatform(Notification notification);

    boolean sendToAuction(JsonNode commandMessage);

}

