package com.procurement.orchestrator.delegate.requisition;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.RequisitionRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.procurement.orchestrator.domain.commands.RequisitionCommandType.CHECK_LOTS_STATUS;

@Component
public class RequisitionCheckLotsStatus implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(RequisitionCheckLotsStatus.class);

    private final RequisitionRestClient requisitionRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public RequisitionCheckLotsStatus(final RequisitionRestClient requisitionRestClient,
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
        final JsonNode relatedLot = processService.getEnquiryRelatedLot(jsonData, processId);
        final JsonNode commandMessage = processService.getCommandMessage(CHECK_LOTS_STATUS, context, relatedLot);
        if (relatedLot != null) {
            final JsonNode responseData = processService.processResponse(
                    requisitionRestClient.execute(commandMessage),
                    context,
                    processId,
                    taskId,
                    commandMessage);
            if (responseData != null) {
                operationService.saveOperationStep(execution, entity, commandMessage);
            }
        }
    }
}

