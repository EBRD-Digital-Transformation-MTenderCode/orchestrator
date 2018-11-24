package com.procurement.orchestrator.delegate.kafka;

import com.procurement.orchestrator.domain.Notification;
import com.procurement.orchestrator.domain.chronograph.ScheduleTask;
import com.procurement.orchestrator.domain.dto.command.CommandMessage;

public class MessageProducerMockImpl implements MessageProducer {

    public boolean sendToChronograph(final ScheduleTask task) {
        return true;
    }

    public boolean sendToPlatform(final Notification notification) {
        return true;
    }

    public boolean sendToAuction(CommandMessage commandMessage) {
        return true;
    }

    public boolean sendToDocGenerator(final CommandMessage commandMessage) {
        return true;
    }
}
