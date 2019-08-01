package com.procurement.orchestrator.delegate.evaluation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.EvaluationRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static com.procurement.orchestrator.domain.commands.EvaluationCommandType.EVALUATE_AWARD;

@Component
public class EvaluationEvaluateAward implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(EvaluationEvaluateAward.class);

    private final EvaluationRestClient evaluationRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public EvaluationEvaluateAward(final EvaluationRestClient evaluationRestClient,
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
        LOG.info(execution.getCurrentActivityId());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        final String taskId = execution.getCurrentActivityId();
        final String processId = execution.getProcessInstanceId();

        final JsonNode commandMessage = processService.getCommandMessage(EVALUATE_AWARD, context, jsonData);
        if (LOG.isDebugEnabled())
            LOG.debug("COMMAND (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(commandMessage) + "'.");

        final ResponseEntity<ResponseDto> response = evaluationRestClient.execute(commandMessage);
        if (LOG.isDebugEnabled()) {
            final ResponseDto body = response.getBody();
            final String result = jsonUtil.toJson(body);
            LOG.debug("RESPONSE FROM SERVICE (" + context.getOperationId() + "): '" + result + "'.");
        }

        final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
        if (LOG.isDebugEnabled())
            LOG.debug("RESPONSE AFTER PROCESSING (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(responseData) + "'.");

        if (responseData != null) {
            processContext(execution, responseData, processId);
            operationService.saveOperationStep(execution, entity, commandMessage, responseData);
        }
    }

    private void processContext(final DelegateExecution execution, final JsonNode responseData, final String operationId) {
        final ArrayNode relatedLots = (ArrayNode) responseData.get("award").get("relatedLots");

        final String lotId = relatedLots.get(0).asText();
        execution.setVariable("lotId", lotId);

        if (LOG.isDebugEnabled())
            LOG.debug("UPDATE CAMUNDA CONTEXT (" + operationId + "): lot id '" + lotId + "'.");
    }


}
