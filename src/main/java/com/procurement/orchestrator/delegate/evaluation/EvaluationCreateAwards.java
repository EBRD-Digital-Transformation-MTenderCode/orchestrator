package com.procurement.orchestrator.delegate.evaluation;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
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
public class EvaluationCreateAwards implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(EvaluationCreateAwards.class);

    private final EvaluationRestClient evaluationRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public EvaluationCreateAwards(final EvaluationRestClient evaluationRestClient,
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
        final String processId = execution.getProcessInstanceId();
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context params = jsonUtil.toObject(Context.class, entity.getJsonParams());
        final String taskId = execution.getCurrentActivityId();
        final JsonNode requestData = jsonUtil.toJsonNode(entity.getResponseData());
        final JsonNode responseData = processService.processResponse(
                evaluationRestClient.createAwards(
                        params.getCpid(),
                        params.getNewStage(),
                        params.getOwner(),
                        params.getCountry(),
                        params.getPmd(),
                        "priceOnly",
                        params.getStartDate(),
                        requestData),
                params,
                processId,
                taskId,
                requestData);
        if (Objects.nonNull(responseData)) {
            operationService.saveOperationStep(
                    execution,
                    entity,
                    processService.addAwardAccessToParams(params, responseData, processId),
                    requestData,
                    processService.addAwardData(requestData, responseData, processId));
        }
    }
}


