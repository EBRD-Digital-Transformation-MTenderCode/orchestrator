package com.procurement.orchestrator.delegate.access;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.AccessRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static com.procurement.orchestrator.domain.commands.AccessCommandType.GET_AWARD_CRITERIA_AND_CONVERSATIONS;

@Component
public class AccessGetAwardCriteriaAndConversions implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(AccessGetAwardCriteriaAndConversions.class);

    private final AccessRestClient accessRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public AccessGetAwardCriteriaAndConversions(
            final AccessRestClient accessRestClient,
            final OperationService operationService,
            final ProcessService processService,
            final JsonUtil jsonUtil
    ) {
        this.accessRestClient = accessRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityId());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final JsonNode requestData = jsonUtil.toJsonNode(entity.getResponseData());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();

        if (requestData != null) {
            final JsonNode commandMessage = processService.getCommandMessage(GET_AWARD_CRITERIA_AND_CONVERSATIONS, context, jsonUtil.empty());
            if (LOG.isDebugEnabled())
                LOG.debug("COMMAND ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(commandMessage));

            final ResponseEntity<ResponseDto> response = accessRestClient.execute(commandMessage);
            if (LOG.isDebugEnabled())
                LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", context.getOperationId(), jsonUtil.toJson(response.getBody()));

            final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
            if (LOG.isDebugEnabled())
                LOG.debug("RESPONSE AFTER PROCESSING ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(responseData));

            if (responseData != null) {
                final boolean isAwardCriteriaAvailable = isAwardCriteriaAvailable(responseData);
                execution.setVariable("availabilityOfAwardCriteria", isAwardCriteriaAvailable);

                final JsonNode step = addCriteriaDetails(requestData, responseData, processId);
                if (LOG.isDebugEnabled())
                    LOG.debug("STEP FOR SAVE ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(step));

                operationService.saveOperationStep(execution, entity, context, commandMessage, step);
            }
        } else {
            if (LOG.isDebugEnabled())
                LOG.debug("Request data is missing. The Access service for checking responses was not called.");
        }

    }

    private JsonNode addCriteriaDetails(final JsonNode jsonData, final JsonNode criteriaData, final String processId) {
        try {
            ((ObjectNode) jsonData).replace("awardCriteria", criteriaData.get("awardCriteria"));
            ((ObjectNode) jsonData).replace("awardCriteriaDetails", criteriaData.get("awardCriteriaDetails"));
            ((ObjectNode) jsonData).replace("conversions", criteriaData.get("conversions"));
            return jsonData;
        } catch (Exception e) {
            processService.terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    private boolean isAwardCriteriaAvailable(final JsonNode criteriaData) {
        return criteriaData.has("awardCriteria") && criteriaData.has("awardCriteriaDetails");
    }

}
