package com.procurement.orchestrator.delegate.stage;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.domain.entity.StageEntity;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CheckStage implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CheckStage.class);
    private static final String OPERATION_ERROR = "Operation for current stage is impossible.";
    private static final String STAGE_ERROR = "Previous stage not found.";
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public CheckStage(final OperationService operationService,
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
        final Context params = jsonUtil.toObject(Context.class, entity.getJsonParams());
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        final StageEntity stageEntity = operationService.getStageParams(params.getCpid(), processId);
        if (stageEntity != null) {
            if (!params.getNewStage().equals(stageEntity.getStage())) {
                processService.terminateProcessWithMessage(params, processId, OPERATION_ERROR);
            } else {
                params.setCountry(stageEntity.getCountry());
                params.setPmd(stageEntity.getPmd());
                params.setPrevStage(stageEntity.getStage());
                operationService.saveOperationStep(execution, entity, params, jsonData);
            }
        } else {
            processService.terminateProcessWithMessage(params, processId, STAGE_ERROR);
        }
    }
}
