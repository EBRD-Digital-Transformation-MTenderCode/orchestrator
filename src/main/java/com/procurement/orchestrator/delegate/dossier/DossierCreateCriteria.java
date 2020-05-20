package com.procurement.orchestrator.delegate.dossier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.DossierRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static com.procurement.orchestrator.domain.commands.DossierCommandType.CREATE_CRITERIA;

@Component
public class DossierCreateCriteria implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(DossierCreateCriteria.class);

    private static final String ATTRIBUTE_CRITERIA = "criteria";
    private static final String ATTRIBUTE_CONVERSIONS = "conversions";
    private static final String ATTRIBUTE_AWARD_CRITERIA_DETAILS = "awardCriteriaDetails";

    private final DossierRestClient dossierRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public DossierCreateCriteria(
            final DossierRestClient dossierRestClient,
            final OperationService operationService,
            final ProcessService processService,
            final JsonUtil jsonUtil
    ) {
        this.dossierRestClient = dossierRestClient;
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
            final JsonNode commandMessage = processService.getCommandMessage(CREATE_CRITERIA, context, requestData);
            if (LOG.isDebugEnabled())
                LOG.debug("COMMAND (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(commandMessage) + "'.");

            final ResponseEntity<ResponseDto> response = dossierRestClient.execute(commandMessage);
            if (LOG.isDebugEnabled())
                LOG.debug("RESPONSE FROM SERVICE (" + context.getOperationId() + "): '" + jsonUtil.toJson(response.getBody()) + "'.");

            final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
            if (LOG.isDebugEnabled())
                LOG.debug("RESPONSE AFTER PROCESSING (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(responseData) + "'.");

            if (responseData != null) {
                final JsonNode step = addCreateCriteriaData(requestData, responseData, processId);
                if (LOG.isDebugEnabled())
                    LOG.debug("STEP FOR SAVE (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(step) + "'.");

                operationService.saveOperationStep(execution, entity, context, commandMessage, step);
            }
        } else {
            if (LOG.isDebugEnabled())
                LOG.debug("Request data is missing. The Dossier service for creating criteria was not called.");
        }

    }

    private JsonNode addCreateCriteriaData(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode tenderNode = (ObjectNode) jsonData.get("tender");

            if (responseData.has(ATTRIBUTE_CRITERIA))
                tenderNode.set(ATTRIBUTE_CRITERIA, responseData.get(ATTRIBUTE_CRITERIA));

            if (responseData.has(ATTRIBUTE_CONVERSIONS))
                tenderNode.set(ATTRIBUTE_CONVERSIONS, responseData.get(ATTRIBUTE_CONVERSIONS));

            if (responseData.has(ATTRIBUTE_AWARD_CRITERIA_DETAILS))
                tenderNode.set(ATTRIBUTE_AWARD_CRITERIA_DETAILS, responseData.get(ATTRIBUTE_AWARD_CRITERIA_DETAILS));

            return jsonData;
        } catch (Exception e) {
            processService.terminateProcess(processId, e.getMessage());
            return null;
        }
    }

}
