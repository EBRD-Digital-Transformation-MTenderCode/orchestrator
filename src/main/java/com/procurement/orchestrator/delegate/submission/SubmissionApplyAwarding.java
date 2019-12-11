package com.procurement.orchestrator.delegate.submission;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.SubmissionRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.procurement.orchestrator.domain.commands.EvaluationCommandType.APPLY_EVALUATED_AWARDS;

@Component
public class SubmissionApplyAwarding implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(SubmissionApplyAwarding.class);

    private final SubmissionRestClient submissionRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public SubmissionApplyAwarding(
        final SubmissionRestClient submissionRestClient,
        final OperationService operationService,
        final ProcessService processService,
        final JsonUtil jsonUtil
    ) {
        this.submissionRestClient = submissionRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityId());
        final String processId = execution.getProcessInstanceId();
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final String taskId = execution.getCurrentActivityId();
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());

        final JsonNode commandMessage = processService.getCommandMessage(APPLY_EVALUATED_AWARDS, context, jsonData);
        if (LOG.isDebugEnabled())
            LOG.debug("COMMAND ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(commandMessage));

        final ResponseEntity<ResponseDto> response = submissionRestClient.execute(commandMessage);
        if (LOG.isDebugEnabled())
            LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", context.getOperationId(), jsonUtil.toJson(response.getBody()));

        final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
        if (LOG.isDebugEnabled())
            LOG.debug("RESPONSE AFTER PROCESSING ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(responseData));

        if (Objects.nonNull(responseData)) {
            final JsonNode step = addBids(context.getOperationId(), jsonData, responseData, processId);
            if (LOG.isDebugEnabled())
                LOG.debug("STEP FOR SAVE ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(step));

            operationService.saveOperationStep(execution, entity, commandMessage, step);
        }
    }

    private JsonNode addBids(
        final String operationId,
        final JsonNode jsonData,
        final JsonNode responseData,
        final String processId
    ) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.set("bids", responseData.get("bids"));
            return jsonData;
        } catch (Exception e) {
            LOG.error("COMMAND (" + operationId + "): Could not add bids.", e);
            processService.terminateProcess(processId, e.getMessage());
            return null;
        }
    }
}

