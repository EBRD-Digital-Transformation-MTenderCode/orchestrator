package com.procurement.orchestrator.delegate;

import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.delegate.test.ChronographProduceTask;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.kafka.MessageProducer;
import com.procurement.orchestrator.kafka.dto.PlatformMessage;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.HashMap;
import java.util.Map;
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

    public SendMessageToPlatform(final MessageProducer messageProducer,
                                 final OperationService operationService,
                                 final JsonUtil jsonUtil) {
        this.messageProducer = messageProducer;
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info("->Data preparation for platform.");
        final Optional<OperationStepEntity> entityOptional = operationService.getPreviousOperationStep(execution);
        if (entityOptional.isPresent()) {
            final OperationStepEntity entity = entityOptional.get();
            final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
            Map<String, String> data = new HashMap<>();
            data.put("token", params.getToken());
            data.put("cpid", params.getCpid());
            data.put("ocid", params.getOcid());
            final PlatformMessage message = new PlatformMessage(
                    params.getOperationId(),
                    jsonUtil.toJson(data)
            );
            messageProducer.sendToPlatform(message);
        }
    }
}
