package com.procurement.orchestrator.delegate.stage;

import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.domain.Rules;
import com.procurement.orchestrator.domain.entity.RequestEntity;
import com.procurement.orchestrator.domain.entity.StageEntity;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.service.RequestService;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.HashMap;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CheckRulesForStageSync implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CheckRulesForStageSync.class);
    private final OperationService operationService;
    private final RequestService requestService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public CheckRulesForStageSync(final OperationService operationService,
                                  final RequestService requestService,
                                  final ProcessService processService,
                                  final JsonUtil jsonUtil) {
        this.operationService = operationService;
        this.requestService = requestService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
        final String processId = execution.getProcessInstanceId();
        final String requestId = (String) execution.getVariable("requestId");
        final RequestEntity requestEntity = requestService.getRequestById(requestId, processId);
        final Params params = jsonUtil.toObject(Params.class, requestEntity.getJsonParams());
        final StageEntity stageEntity = operationService.getStageParams(params.getCpid(), processId);
        final Rules rules = new Rules(
                params.getNewStage(),
                stageEntity.getStage(),
                stageEntity.getCountry(),
                stageEntity.getPmd(),
                stageEntity.getPhase(),
                params.getOperationType());
        if (!operationService.isRulesExist(rules)) {
            execution.setVariable("checkRules", 0);
            execution.setVariable("message", "Operation for current stage is impossible.");
        } else {
            processService.startProcess(params, new HashMap<>());
        }
    }
}
