package com.procurement.orchestrator.delegate.tender.access;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.rest.AccessRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Objects;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AccessSetSuspended implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(AccessSetSuspended.class);

    private final AccessRestClient accessRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public AccessSetSuspended(final AccessRestClient accessRestClient,
                              final OperationService operationService,
                              final ProcessService processService,
                              final JsonUtil jsonUtil) {
        this.accessRestClient = accessRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
        final String processId = execution.getProcessInstanceId();
        final String operationId = params.getOperationId();
        final Boolean suspended = (Boolean) execution.getVariableLocal("suspended");
        try {
            final JsonNode responseData = processService.processResponse(
                    accessRestClient.setSuspended(params.getCpid(), suspended),
                    processId,
                    operationId);
            if (Objects.nonNull(responseData))
                operationService.saveOperationStep(
                        execution,
                        entity,
                        responseData);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            processService.processException(e.getMessage(), processId);
        }
    }
}