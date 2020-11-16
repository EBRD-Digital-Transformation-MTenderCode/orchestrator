package com.procurement.orchestrator.delegate.requisition;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.RequisitionRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static com.procurement.orchestrator.domain.commands.RequisitionCommandType.SET_TENDER_UNSUSPENDED;

@Component
public class RequisitionSetTenderUnsuspended implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(RequisitionSetTenderUnsuspended.class);

    private final RequisitionRestClient requisitionRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public RequisitionSetTenderUnsuspended(final RequisitionRestClient requisitionRestClient,
                                           final OperationService operationService,
                                           final ProcessService processService,
                                           final JsonUtil jsonUtil) {
        this.requisitionRestClient = requisitionRestClient;
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

        final JsonNode commandMessage = processService.getCommandMessage(SET_TENDER_UNSUSPENDED, context, jsonUtil.empty());
        if (LOG.isDebugEnabled())
            LOG.debug("COMMAND ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(commandMessage));

        final ResponseEntity<ResponseDto> response = requisitionRestClient.execute(commandMessage);
        if (LOG.isDebugEnabled())
            LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", context.getOperationId(), jsonUtil.toJson(response.getBody()));

        JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
        if (LOG.isDebugEnabled())
            LOG.debug("RESPONSE AFTER PROCESSING ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(responseData));

        if (responseData != null) {
            context.setOperationType("unsuspendTender");
            if (LOG.isDebugEnabled())
                LOG.debug("CONTEXT FOR SAVE ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(context));

            execution.setVariable("pmd", context.getPmd());

            final JsonNode step = processService.addTenderUnsuspendData(jsonData, responseData, processId);
            if (LOG.isDebugEnabled())
                LOG.debug("STEP FOR SAVE ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(step));

            operationService.saveOperationStep(execution, entity, context, commandMessage, step);
        }
    }
}
