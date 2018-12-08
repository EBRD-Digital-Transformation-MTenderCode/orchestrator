package com.procurement.orchestrator.delegate.notification;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.delegate.kafka.MessageProducer;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.command.CommandMessage;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.service.NotificationService;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.procurement.orchestrator.domain.commands.AuctionCommandType.LAUNCH;

@Component
public class SendMessageToAuction implements JavaDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(SendMessageToAuction.class);

    private final ProcessService processService;
    private final NotificationService notificationService;
    private final OperationService operationService;
    private final MessageProducer messageProducer;
    private final JsonUtil jsonUtil;

    public SendMessageToAuction(final ProcessService processService,
                                final NotificationService notificationService,
                                final OperationService operationService,
                                final MessageProducer messageProducer,
                                final JsonUtil jsonUtil) {
        this.processService = processService;
        this.notificationService = notificationService;
        this.operationService = operationService;
        this.messageProducer = messageProducer;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final String processId = execution.getProcessInstanceId();
        final JsonNode rqData = processService.getAuctionLaunchData(jsonData, processId);
        final CommandMessage commandMessage = notificationService.getCommandMessage(LAUNCH, context, rqData);
        messageProducer.sendToAuction(commandMessage);
        operationService.saveOperationStep(
                execution,
                entity,
                context,
                jsonUtil.toJsonNode(commandMessage),
                jsonData);
    }

}

