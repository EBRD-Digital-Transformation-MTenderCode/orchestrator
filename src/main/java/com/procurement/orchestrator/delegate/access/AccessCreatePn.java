package com.procurement.orchestrator.delegate.access;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Params;
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
public class AccessCreatePn implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(AccessCreatePn.class);

    private final AccessRestClient accessRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public AccessCreatePn(final AccessRestClient accessRestClient,
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
        final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();
        final JsonNode responseData = processService.processResponse(
            accessRestClient.createPn(
                params.getNewStage(),
                params.getCountry(),
                params.getPmd(),
                params.getOwner(),
                params.getStartDate(),
                jsonData),
            params,
            processId,
            taskId);
        if (Objects.nonNull(responseData))
            operationService.saveOperationStep(
                execution,
                entity,
                addDataToParams(params, responseData, processId),
                processService.setCn(jsonData, responseData, processId));
    }

    private Params addDataToParams(final Params params, final JsonNode responseData, final String processId) {
        params.setCpid(processService.getText("ocid", responseData, processId));
        return processService.addAccessToParams(params, "tender", params.getCpid(), responseData, processId);
    }
}
