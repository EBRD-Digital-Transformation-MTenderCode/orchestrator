package com.procurement.orchestrator.delegate.stage;

import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.domain.entity.StageEntity;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GetStageParams implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(GetStageParams.class);
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public GetStageParams(final OperationService operationService,
                          final ProcessService processService,
                          final JsonUtil jsonUtil) {
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
        final String processId = execution.getProcessInstanceId();
        StageEntity stageEntity = operationService.getStageParams(params.getCpid(), processId);
        if (!params.getStage().equals(stageEntity.getStage()))
            params.setPreviousStage(stageEntity.getStage());
        params.setCountry(stageEntity.getCountry());
        params.setPmd(stageEntity.getPmd());
        operationService.saveOperationStep(
                execution,
                entity,
                params);

    }
}
