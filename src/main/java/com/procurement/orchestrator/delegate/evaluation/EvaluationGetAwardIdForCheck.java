package com.procurement.orchestrator.delegate.evaluation;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.EvaluationRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.procurement.orchestrator.domain.commands.EvaluationCommandType.GET_AWARD_FOR_CAN;
import static com.procurement.orchestrator.domain.commands.EvaluationCommandType.GET_AWARD_ID_FOR_CHECK;

@Component
public class EvaluationGetAwardIdForCheck implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(EvaluationGetAwardIdForCheck.class);

    private final EvaluationRestClient evaluationRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public EvaluationGetAwardIdForCheck(final EvaluationRestClient evaluationRestClient,
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
        final JsonNode commandMessage = processService.getCommandMessage(GET_AWARD_ID_FOR_CHECK, context, jsonUtil.empty());
        JsonNode responseData = processService.processResponse(
                evaluationRestClient.execute(commandMessage),
                context,
                processId,
                taskId,
                commandMessage);
        if (responseData != null) {
            operationService.saveOperationStep(
                    execution,
                    entity,
                    commandMessage,
                    processService.addAwardId(jsonData, responseData, processId));
        }
    }
}
