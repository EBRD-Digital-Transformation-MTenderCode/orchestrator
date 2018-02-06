package com.procurement.orchestrator.delegate.tender.clarification;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.rest.ClarificationRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Objects;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ClarificationCheckEnquiries implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ClarificationCheckEnquiries.class);

    private final ClarificationRestClient clarificationRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public ClarificationCheckEnquiries(final ClarificationRestClient clarificationRestClient,
                                       final OperationService operationService,
                                       final ProcessService processService,
                                       final JsonUtil jsonUtil) {
        this.clarificationRestClient = clarificationRestClient;
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
        try {
            final JsonNode responseData = processService.processResponse(
                    clarificationRestClient.checkEnquiries(params.getCpid(), params.getStage()),
                    processId,
                    operationId);
            if (Objects.nonNull(responseData)) {
                final Boolean allAnswered = getAllAnswered(responseData, processId, operationId);
                if (allAnswered != null) {
                    execution.setVariable("checkEnquiries", (allAnswered ? 1 : 2));
                } else {
                    final String endDate = getTenderPeriodEndDate(responseData, processId, operationId);
                    execution.setVariable("checkEnquiries", 3);
                    if (endDate != null) {
                        params.setEndDate(endDate);
                    }
                }
                operationService.saveOperationStep(
                        execution,
                        entity,
                        responseData);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            processService.processException(e.getMessage(), execution.getProcessInstanceId());
        }
    }

    private Boolean getAllAnswered(final JsonNode responseData,
                                   final String processId,
                                   final String operationId) {
        try {
            if (responseData.get("allAnswered") != null) {
                return responseData.get("allAnswered").asBoolean();
            } else return null;
        } catch (Exception e) {
            processService.processError(e.getMessage(), processId, operationId);
            return null;
        }
    }

    private String getTenderPeriodEndDate(final JsonNode responseData,
                                          final String processId,
                                          final String operationId) {
        try {
            if (responseData.get("tenderPeriodEndDate") != null) {
                return responseData.get("tenderPeriodEndDate").asText();
            } else return null;
        } catch (Exception e) {
            processService.processError(e.getMessage(), processId, operationId);
            return null;
        }
    }
}
