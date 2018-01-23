package com.procurement.orchestrator.delegate;

import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.delegate.test.ChronographProduceTask;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.kafka.MessageProducer;
import com.procurement.orchestrator.kafka.dto.ChronographTask;
import com.procurement.orchestrator.kafka.dto.PlatformMessage;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Optional;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SendMessageToPlatform implements JavaDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(ChronographProduceTask.class);

    private final MessageProducer messageProducer;

    private final OperationService operationService;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;

    public SendMessageToPlatform(final MessageProducer messageProducer,
                                  final OperationService operationService,
                                  final JsonUtil jsonUtil,
                                  final DateUtil dateUtil) {
        this.messageProducer = messageProducer;
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info("->Data preparation for platform.");
        final Optional<OperationStepEntity> entityOptional = operationService.getOperationStep(execution);
        if (entityOptional.isPresent()) {
            final OperationStepEntity entity = entityOptional.get();
            final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
            PlatformMessage message = new PlatformMessage(params.getOperationId(), params.getToken());
            messageProducer.sendToPlatform(message);
        }
    }
}
