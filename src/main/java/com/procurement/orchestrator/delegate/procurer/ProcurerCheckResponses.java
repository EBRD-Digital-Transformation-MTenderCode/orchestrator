package com.procurement.orchestrator.delegate.procurer;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.ProcurerRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static com.procurement.orchestrator.domain.commands.ProcurerCommandType.CHECK_RESPONSES;

@Component
public class ProcurerCheckResponses implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ProcurerCheckResponses.class);

    private final ProcurerRestClient procurerRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public ProcurerCheckResponses(
            final ProcurerRestClient procurerRestClient,
            final OperationService operationService,
            final ProcessService processService,
            final JsonUtil jsonUtil
    ) {
        this.procurerRestClient = procurerRestClient;
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
            final JsonNode commandMessage = processService.getCommandMessage(CHECK_RESPONSES, context, requestData);
            if (LOG.isDebugEnabled())
                LOG.debug("COMMAND (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(commandMessage) + "'.");

            final ResponseEntity<ResponseDto> response = procurerRestClient.execute(commandMessage);
            if (LOG.isDebugEnabled())
                LOG.debug("RESPONSE FROM SERVICE (" + context.getOperationId() + "): '" + jsonUtil.toJson(response.getBody()) + "'.");

            final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
            if (LOG.isDebugEnabled())
                LOG.debug("RESPONSE AFTER PROCESSING (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(responseData) + "'.");

            if (responseData != null) {
                operationService.saveOperationStep(execution, entity, context, commandMessage);
            }
        } else {
            if (LOG.isDebugEnabled())
                LOG.debug("Request data is missing. The Procurer service for checking responses was not called.");
        }

    }


}
