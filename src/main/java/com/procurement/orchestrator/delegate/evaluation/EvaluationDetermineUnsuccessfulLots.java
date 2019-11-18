package com.procurement.orchestrator.delegate.evaluation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

import static com.procurement.orchestrator.domain.commands.EvaluationCommandType.AWARDS_CANCELLATION;
import static com.procurement.orchestrator.domain.commands.EvaluationCommandType.GET_UNSUCCSESS_LOTS;

@Component
public class EvaluationDetermineUnsuccessfulLots implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(EvaluationDetermineUnsuccessfulLots.class);

    private final EvaluationRestClient evaluationRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public EvaluationDetermineUnsuccessfulLots(
            final EvaluationRestClient evaluationRestClient,
            final OperationService operationService,
            final ProcessService processService,
            final JsonUtil jsonUtil
    ) {
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
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();

        final JsonNode commandMessage = processService.getCommandMessage(GET_UNSUCCSESS_LOTS, context, jsonData);
        LOG.debug("COMMAND ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(commandMessage));

        final ResponseEntity<ResponseDto> response = evaluationRestClient.execute(commandMessage);
        LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", context.getOperationId(), jsonUtil.toJson(response.getBody()));

        final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
        LOG.debug("RESPONSE AFTER PROCESSING ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(responseData));

        if (responseData != null) {
            final boolean hasUnsuccessfulLots = hasUnsuccessfulLots(responseData);
            execution.setVariable("availabilityOfUnsuccessfulLots", hasUnsuccessfulLots);

            final JsonNode step = addUnsuccessfulLots(jsonData, responseData, processId);
            if (LOG.isDebugEnabled())
                LOG.debug("STEP FOR SAVE ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(step));

            operationService.saveOperationStep(execution, entity, context, commandMessage, step);
        }
    }

    private boolean hasUnsuccessfulLots(final JsonNode lotsData) {
        return !lotsData.has("unsuccessfulLots") || (lotsData.get("unsuccessfulLots")).size() == 0;
    }

    private JsonNode addUnsuccessfulLots(final JsonNode jsonData, final JsonNode lotsData, final String processId) {
        try {
            ((ObjectNode) jsonData).replace("unsuccessfulLots", lotsData.get("unsuccessfulLots"));
            return jsonData;
        } catch (Exception e) {
            processService.terminateProcess(processId, e.getMessage());
            return null;
        }
    }

}
