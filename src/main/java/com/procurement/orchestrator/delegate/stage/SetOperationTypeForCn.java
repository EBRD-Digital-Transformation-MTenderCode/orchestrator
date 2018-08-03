package com.procurement.orchestrator.delegate.stage;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Stage;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SetOperationTypeForCn implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(SetOperationTypeForCn.class);
    private static final String OPERATION_ERROR = "Operation for current stage is impossible.";
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public SetOperationTypeForCn(final OperationService operationService,
                                 final ProcessService processService,
                                 final JsonUtil jsonUtil) {
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
        final String processId = execution.getProcessInstanceId();
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        switch (Stage.fromValue(context.getPrevStage())) {
            case PN:
                context.setOperationType("createCNonPN");
                execution.setVariable("operationType", "createCNonPN");
                operationService.saveOperationStep(execution, entity, context, jsonData);
                break;
            case PIN:
                context.setOperationType("createCNonPIN");
                execution.setVariable("operationType", "createCNonPIN");
                operationService.saveOperationStep(execution, entity, context, jsonData);
                break;
            case PS:
                context.setOperationType("updateCN");
                execution.setVariable("operationType", "updateCN");
                operationService.saveOperationStep(execution, entity, context, jsonData);
                break;
            case PQ:
                context.setOperationType("updateCN");
                execution.setVariable("operationType", "updateCN");
                operationService.saveOperationStep(execution, entity, context, jsonData);
                break;
            case EV:
                context.setOperationType("updateCN");
                execution.setVariable("operationType", "updateCN");
                operationService.saveOperationStep(execution, entity, context, jsonData);
                break;
            default:
                processService.terminateProcessWithMessage(context, processId, OPERATION_ERROR);
        }
    }
}
