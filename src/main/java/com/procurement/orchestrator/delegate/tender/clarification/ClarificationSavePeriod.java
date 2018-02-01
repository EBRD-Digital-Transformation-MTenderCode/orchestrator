package com.procurement.orchestrator.delegate.tender.clarification;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.rest.ClarificationRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Objects;
import java.util.Optional;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
        final Optional<OperationStepEntity> entityOptional = operationService.getPreviousOperationStep(execution);
        if (entityOptional.isPresent()) {
            final OperationStepEntity entity = entityOptional.get();
            final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
            final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
            final String processId = execution.getProcessInstanceId();
            final String operationId = params.getOperationId();
            try {
                final JsonNode responseData = processService.processResponse(
                        clarificationRestClient.savePeriod(
                                params.getCpid(),
                                params.getCountry(),
                                params.getPmd(),
                                "ps",
                                params.getOwner(),
                                getStartDate(jsonData, processId, operationId),
                                getEndDate(jsonData, processId, operationId)),
                        processId,
                        operationId);
                if (Objects.nonNull(responseData))
                operationService.saveOperationStep(
                        execution,
                        entity,
                        addEnquiryPeriod(jsonData, responseData, processId, operationId));
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                processService.processException(e.getMessage(), processId);
            }
        }
    }

    private String getStartDate(final JsonNode jsonData,
                                final String processId,
                                final String operationId) {
        try {
            final JsonNode tenderNode = jsonData.get("tender");
            final JsonNode tenderPeriodNode = tenderNode.get("tenderPeriod");
            return tenderPeriodNode.get("startDate").asText();
        } catch (Exception e) {
            processService.processError(e.getMessage(), processId, operationId);
        }
        return null;
    }

    private String getEndDate(final JsonNode jsonData,
                              final String processId,
                              final String operationId) {
        try {
            final JsonNode tenderNode = jsonData.get("tender");
            final JsonNode tenderPeriodNode = tenderNode.get("tenderPeriod");
            return tenderPeriodNode.get("endDate").asText();
        } catch (Exception e) {
            processService.processError(e.getMessage(), processId, operationId);
            return null;
        }
    }

    private JsonNode addEnquiryPeriod(final JsonNode jsonData,
                                      final JsonNode responseData,
                                      final String processId,
                                      final String operationId) {
        try {
            final JsonNode tenderNode = jsonData.get("tender");
            ObjectNode enquiryPeriodNode = ((ObjectNode) tenderNode).putObject("enquiryPeriod");
            enquiryPeriodNode
                    .put("startDate", responseData.get("startDate").asText())
                    .put("endDate", responseData.get("endDate").asText());
            return jsonData;
        } catch (Exception e) {
            processService.processError(e.getMessage(), processId, operationId);
            return null;
        }
    }
}
