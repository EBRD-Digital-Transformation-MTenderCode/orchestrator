package com.procurement.orchestrator.delegate;

import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.cassandra.model.Params;
import com.procurement.orchestrator.kafka.MessageProducer;
import com.procurement.orchestrator.kafka.dto.PlatformMessage;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SendMessageToPlatform implements JavaDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(SendMessageToPlatform.class);

    private final MessageProducer messageProducer;

    private final OperationService operationService;

    private final JsonUtil jsonUtil;

    public SendMessageToPlatform(final MessageProducer messageProducer,
                                 final OperationService operationService,
                                 final JsonUtil jsonUtil) {
        this.messageProducer = messageProducer;
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());


        final PlatformMessage message = new PlatformMessage(
                true,
                params.getOperationId(),
                params.getToken(),
                params.getCpid(),
                params.getOcid(),
                null);
        messageProducer.sendToPlatform(message);
    }
}
