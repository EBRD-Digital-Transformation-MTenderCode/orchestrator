package com.procurement.orchestrator.delegate.stage;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Params;
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
public class SetOperationTypeForPin implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(SetOperationTypeForPin.class);
    private static final String OPERATION_ERROR = "Operation for current stage is impossible.";
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public SetOperationTypeForPin(final OperationService operationService,
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
        final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        switch (Stage.fromValue(params.getPrevStage())) {
            case PN:
                params.setOperationType("createPINonPN");
                execution.setVariable("operationType", "createPINonPN");
                operationService.saveOperationStep(execution, entity, params, jsonData);
                break;
            case PIN:
                params.setOperationType("updatePIN");
                execution.setVariable("operationType", "updatePIN");
                operationService.saveOperationStep(execution, entity, params, jsonData);
                break;
            default:
                processService.terminateProcessWithMessage(params, processId, OPERATION_ERROR);
        }
    }
}
