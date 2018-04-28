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
public class EvaluationUpdateAward implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(EvaluationUpdateAward.class);

    private final EvaluationRestClient evaluationRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public EvaluationUpdateAward(final EvaluationRestClient evaluationRestClient,
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
        final JsonNode requestData = jsonUtil.toJsonNode(entity.getResponseData());
        final String taskId = execution.getCurrentActivityId();
        final String processId = execution.getProcessInstanceId();
        final JsonNode responseData = processService.processResponse(
                evaluationRestClient.updateAward(
                        params.getCpid(),
                        params.getNewStage(),
                        params.getToken(),
                        params.getOwner(),
                        params.getStartDate(),
                        requestData),
                params,
                processId,
                taskId,
                requestData);
        if (Objects.nonNull(responseData)) {
            processParams(execution, responseData, processId);
            operationService.saveOperationStep(execution, entity, requestData, responseData);
        }
    }

    private void processParams(final DelegateExecution execution, final JsonNode responseData, final String processId) {
        final String awardStatusDetails = processService.getAwardStatusDetails(responseData, processId);
        if (awardStatusDetails.equals("active")) {
            execution.setVariable("awardStatusDetails", "active");
        } else {
            execution.setVariable("awardStatusDetails", "unsuccessful");
        }
    }
}