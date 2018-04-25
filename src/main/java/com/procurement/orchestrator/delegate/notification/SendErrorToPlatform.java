package com.procurement.orchestrator.delegate.notification;

import com.procurement.orchestrator.config.kafka.MessageProducer;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.domain.PlatformMessage;
import com.procurement.orchestrator.domain.entity.RequestEntity;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.RequestService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SendErrorToPlatform implements JavaDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(SendErrorToPlatform.class);

    private final MessageProducer messageProducer;
    private final RequestService requestService;
    private final JsonUtil jsonUtil;
    private final OperationService operationService;

    public SendErrorToPlatform(final MessageProducer messageProducer,
                               final RequestService requestService,
                               final OperationService operationService,
                               final JsonUtil jsonUtil) {
        this.messageProducer = messageProducer;
        this.requestService = requestService;
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
        final String requestId = (String) execution.getVariable("requestId");
        final String message = (String) execution.getVariable("message");
        final RequestEntity requestEntity = requestService.getRequestById(requestId, execution.getProcessInstanceId());
        final Params params = jsonUtil.toObject(Params.class, requestEntity.getJsonParams());
        final PlatformMessage platformMessage = new PlatformMessage(
                false,
                params.getOperationId(),
                params.getOperationType(),
                params.getCpid(),
                params.getNewStage(),
                message);
        messageProducer.sendToPlatform(platformMessage);
        operationService.saveOperationException(
                execution.getProcessInstanceId(),
                execution.getCurrentActivityId(),
                params.getOperationId(),
                jsonUtil.toJsonNode(params),
                jsonUtil.toJsonNode(jsonUtil.toJson(message)));
    }
}
