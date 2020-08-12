package com.procurement.orchestrator.delegate.context;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.OperationType;
import com.procurement.orchestrator.domain.Stage;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SaveCancelCanContext implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(SaveCancelCanContext.class);
    private final OperationService operationService;
    private final JsonUtil jsonUtil;

    public SaveCancelCanContext(final OperationService operationService,
                                final JsonUtil jsonUtil) {
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityId());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        operationService.saveContext(context);

        if (context.getOperationType().equals(OperationType.CANCEL_CAN_CONTRACT.value())) {
            context.setStage(Stage.AC.value());
            context.setPhase("empty");
            final JsonNode jsonData = jsonUtil.toJsonNode(entity.getRequestData());
            final String acId = jsonData.get("data").get("contract").get("id").asText();
            context.setOcid(acId);
            operationService.saveContractContext(acId, context);
        }

        operationService.saveOperationStep(execution, entity);
    }
}
