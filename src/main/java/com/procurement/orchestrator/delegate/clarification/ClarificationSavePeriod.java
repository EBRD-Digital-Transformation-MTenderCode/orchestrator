package com.procurement.orchestrator.delegate.clarification;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.ClarificationRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static com.procurement.orchestrator.domain.commands.ClarificationCommandType.SAVE_PERIOD;

@Component
public class ClarificationSavePeriod implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ClarificationSavePeriod.class);

    private final ClarificationRestClient clarificationRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public ClarificationSavePeriod(final ClarificationRestClient clarificationRestClient,
                                   final OperationService operationService,
                                   final ProcessService processService,
                                   final JsonUtil jsonUtil) {
        this.clarificationRestClient = clarificationRestClient;
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

        final Context commandContext = new Context.Builder()
                .setRequestId(context.getRequestId())
                .setCpid(context.getCpid())
                .setOcid(getOcid(context))
                .setOwner(context.getOwner())
                .build();
        final JsonNode enquiryPeriod = processService.getEnquiryPeriod(jsonData, processId);

        final JsonNode commandMessage = processService.getCommandMessage(SAVE_PERIOD, commandContext, enquiryPeriod);
        if (LOG.isDebugEnabled()) {
            LOG.debug("COMMAND ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(commandMessage));
        }

        final ResponseEntity<ResponseDto> response = clarificationRestClient.execute(commandMessage);
        if (LOG.isDebugEnabled()) {
            LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", context.getOperationId(), jsonUtil.toJson(response.getBody()));
        }

        final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
        if (LOG.isDebugEnabled()) {
            LOG.debug("RESPONSE AFTER PROCESSING ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(responseData));
        }

        if (responseData != null) {
            operationService.saveOperationStep(execution, entity, commandMessage, jsonData);
        }
    }

    public String getOcid(final Context context) {
        if (context.getOcidCn() != null)
            return context.getOcidCn();
        else
            return context.getOcid();
    }
}
