package com.procurement.orchestrator.delegate.notification;

import com.procurement.orchestrator.delegate.kafka.MessageProducer;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Notification;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.service.NotificationService;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SendEnquiryMessages implements JavaDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(SendEnquiryMessages.class);

    private final NotificationService notificationService;
    private final OperationService operationService;
    private final MessageProducer messageProducer;
    private final JsonUtil jsonUtil;

    public SendEnquiryMessages(final NotificationService notificationService,
                               final OperationService operationService,
                               final MessageProducer messageProducer,
                               final JsonUtil jsonUtil) {
        this.notificationService = notificationService;
        this.operationService = operationService;
        this.messageProducer = messageProducer;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final Notification notificationEO = notificationService.getNotificationForPlatform(context);
        if (notificationEO != null) {
            messageProducer.sendToPlatform(notificationEO);
            operationService.saveOperationStep(
                    execution,
                    entity,
                    context,
                    jsonUtil.toJsonNode(notificationEO));
        }

        final Notification notificationCA = notificationService.getNotificationForPlatformCA(context);
        if (notificationCA != null) {
            messageProducer.sendToPlatform(notificationCA);
            operationService.saveOperationStep(
                    execution,
                    entity,
                    context,
                    jsonUtil.toJsonNode(notificationCA));
        }
    }
}
