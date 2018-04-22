package com.procurement.orchestrator.delegate.evaluation;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.EvaluationRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Objects;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EvaluationGetAwards implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(EvaluationGetAwards.class);

    private final EvaluationRestClient evaluationRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public EvaluationGetAwards(final EvaluationRestClient evaluationRestClient,
                               final OperationService operationService,
                               final ProcessService processService,
                               final JsonUtil jsonUtil) {
        this.evaluationRestClient = evaluationRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
        final String taskId = execution.getCurrentActivityId();
        final String processId = execution.getProcessInstanceId();
        final JsonNode responseData = processService.processResponse(
                evaluationRestClient.getAwards(
                        params.getCpid(),
                        params.getNewStage(),
                        params.getCountry(),
                        params.getPmd()),
                params,
                processId,
                taskId,
                jsonUtil.empty());
        if (Objects.nonNull(responseData)) {
            operationService.saveOperationStep(execution, entity, jsonUtil.empty(), responseData);
        }
    }
}
