package com.procurement.orchestrator.delegate.fs;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.rest.AccessRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Optional;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FsAccessCreateFs implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(FsAccessCreateFs.class);

    private final AccessRestClient accessRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;


    public FsAccessCreateFs(final AccessRestClient accessRestClient,
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
        final Optional<OperationStepEntity> entityOptional = operationService.getPreviousOperationStep(execution);
        if (entityOptional.isPresent()) {
            final OperationStepEntity entity = entityOptional.get();
            final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
            final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
            try {
                final JsonNode responseData = processService.processResponse(
                        accessRestClient.createFs(
                                params.getOwner(),
                                jsonData),
                        execution.getProcessInstanceId(),
                        params.getOperationId());
                operationService.saveOperationStep(
                        execution,
                        entity,
                        addDataToParams(
                                params,
                                responseData,
                                execution.getProcessInstanceId(),
                                params.getOperationId()),
                        responseData);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                processService.processException(e.getMessage(), execution.getProcessInstanceId());
            }
        }
    }

    private Params addDataToParams(final Params params,
                                   final JsonNode responseData,
                                   final String processId,
                                   final String operationId) {
        try {
            if (responseData.get("token") != null) {
                params.setToken(responseData.get("token").asText());
            }
            if (responseData.get("cpid") != null) {
                params.setCpid(responseData.get("cpid").asText());
            }
            return params;
        } catch (Exception e) {
            processService.processError(e.getMessage(), processId, operationId);
        }
        return null;
    }
}
