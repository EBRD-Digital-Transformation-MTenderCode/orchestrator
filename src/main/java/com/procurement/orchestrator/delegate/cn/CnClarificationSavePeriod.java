package com.procurement.orchestrator.delegate.cn;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.cassandra.model.OperationEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.domain.constant.ResponseMessageType;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import com.procurement.orchestrator.rest.ClarificationRestClient;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Optional;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CnClarificationSavePeriod implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CnClarificationSavePeriod.class);

    private final ClarificationRestClient clarificationRestClient;

    private final OperationService operationService;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;

    public CnClarificationSavePeriod(final ClarificationRestClient clarificationRestClient,
                                     final OperationService operationService,
                                     final JsonUtil jsonUtil,
                                     final DateUtil dateUtil) {
        this.clarificationRestClient = clarificationRestClient;
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info("->Data preparation for E-Clarification.");
        final String txId = execution.getProcessBusinessKey();
        final Optional<OperationEntity> entityOptional = operationService.getLastOperation(txId);
        if (entityOptional.isPresent()) {
            LOG.info("->Send data to E-Clarification.");
            final OperationEntity entity = entityOptional.get();
            final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
            final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
            final String cpId = jsonData.get("ocid").asText();
            final JsonNode tenderPeriodNode = getTenderPeriod(jsonData);
            final String startDate = tenderPeriodNode.get("startDate").asText();
            final String endDate = tenderPeriodNode.get("endDate").asText();
            try {
                final ResponseEntity<ResponseDto> responseEntity = clarificationRestClient.postSavePeriod(
                        cpId,
                        params.getCountry(),
                        params.getPmd(),
                        "ps",
                        params.getOwner(),
                        startDate,
                        endDate);

                JsonNode responseData = jsonUtil.toJsonNode(responseEntity.getBody().getData());
                JsonNode jsonWithEnquiryPeriod = addEnquiryPeriod(jsonData, responseData);
                operationService.saveOperation(getEntity(entity, jsonWithEnquiryPeriod));
            } catch (Exception e) {
                LOG.error(e.getMessage());
                throw new BpmnError("TR_EXCEPTION", ResponseMessageType.SERVICE_EXCEPTION.value());
            }
        }
    }

    private JsonNode getTenderPeriod(JsonNode jsonData) {
        final JsonNode tenderNode = jsonData.get("tender");
        return tenderNode.get("tenderPeriod");
    }

    private JsonNode addEnquiryPeriod(JsonNode jsonData, JsonNode responseData) {
        final JsonNode tenderNode = jsonData.get("tender");
        ObjectNode enquiryPeriodNode = ((ObjectNode) tenderNode).putObject("enquiryPeriod");
        enquiryPeriodNode
                .put("startDate", responseData.get("startDate").asText())
                .put("endDate", responseData.get("endDate").asText());

        return jsonData;
    }

    private OperationEntity getEntity(OperationEntity entity, JsonNode jsonData) {
        entity.setJsonData(jsonUtil.toJson(jsonData));
        entity.setDate(dateUtil.getNowUTC());
        return entity;
    }

}
