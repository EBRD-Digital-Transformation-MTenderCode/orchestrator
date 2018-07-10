package com.procurement.orchestrator.delegate.access;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.AccessRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Objects;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AccessUpdateStatusDetailsById implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(AccessUpdateStatusDetailsById.class);

    private final AccessRestClient accessRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public AccessUpdateStatusDetailsById(final AccessRestClient accessRestClient,
                                         final OperationService operationService,
                                         final ProcessService processService,
                                         final JsonUtil jsonUtil) {
        this.accessRestClient = accessRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context params = jsonUtil.toObject(Context.class, entity.getJsonParams());
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityName();
        final String lotId = processService.getLotId(jsonData, processId);
        final JsonNode responseData = processService.processResponse(
                accessRestClient.updateStatusDetailsById(
                        params.getCpid(),
                        params.getNewStage(),
                        "awarded",
                        lotId),
                params,
                processId,
                taskId,
                jsonData);
        if (Objects.nonNull(responseData))
            operationService.saveOperationStep(execution, entity, jsonData,
                    processService.addUpdatedLot(jsonData, responseData, processId));
    }
}
