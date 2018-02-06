package com.procurement.orchestrator.delegate.tender.clarification;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.rest.ClarificationRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Objects;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ClarificationCreateEnquiry implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ClarificationCreateEnquiry.class);

    private final ClarificationRestClient clarificationRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;

    public ClarificationCreateEnquiry(final ClarificationRestClient clarificationRestClient,
                                      final OperationService operationService,
                                      final ProcessService processService,
                                      final JsonUtil jsonUtil,
                                      final DateUtil dateUtil) {
        this.clarificationRestClient = clarificationRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
        final String processId = execution.getProcessInstanceId();
        final String operationId = params.getOperationId();
        try {
            final JsonNode responseData = processService.processResponse(
                    clarificationRestClient.createEnquiry(
                            params.getCpid(),
                            params.getStage(),
                            params.getOwner(),
                            dateUtil.format(dateUtil.localDateTimeNowUTC()),
                            jsonData),
                    processId,
                    operationId);
            if (Objects.nonNull(responseData))
                operationService.saveOperationStep(
                        execution,
                        entity,
                        addDataToParams(params, responseData, processId, operationId),
                        responseData);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            processService.processException(e.getMessage(), processId);
        }
    }

    private Params addDataToParams(final Params params,
                                   final JsonNode responseData,
                                   final String processId,
                                   final String operationId) {
        params.setToken(processService.getValue("token", responseData, processId, operationId));
        return params;
    }
}