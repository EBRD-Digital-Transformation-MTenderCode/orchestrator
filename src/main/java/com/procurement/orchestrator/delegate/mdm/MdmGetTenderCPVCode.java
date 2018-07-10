package com.procurement.orchestrator.delegate.mdm;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.CommandMessage;
import com.procurement.orchestrator.domain.dto.CommandType;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.MdmRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class MdmGetTenderCPVCode implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(MdmGetTenderCPVCode.class);

    private final MdmRestClient mdmRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public MdmGetTenderCPVCode(final MdmRestClient mdmRestClient,
                               final OperationService operationService,
                               final ProcessService processService,
                               final JsonUtil jsonUtil) {
        this.mdmRestClient = mdmRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final JsonNode prevData = jsonUtil.toJsonNode(entity.getResponseData());
        final Context params = jsonUtil.toObject(Context.class, entity.getJsonParams());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityName();
        final JsonNode rqData = processService.getClassificationOfTender(prevData, processId);
        final CommandMessage commandMessage = processService.getCommandMessage(CommandType.TENDER_CPV, params, rqData);
        JsonNode responseData = null;
        if (Objects.nonNull(rqData))
            responseData = processService.processResponse(
                    mdmRestClient.execute(commandMessage),
                    params,
                    processId,
                    taskId,
                    jsonUtil.toJsonNode(commandMessage));
        if (Objects.nonNull(responseData))
            operationService.saveOperationStep(
                    execution,
                    entity,
                    jsonUtil.toJsonNode(commandMessage),
                    processService.setClassificationOfTender(prevData, responseData, processId));
    }
}
