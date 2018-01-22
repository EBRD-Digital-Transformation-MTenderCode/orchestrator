package com.procurement.orchestrator.delegate.cn;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import com.procurement.orchestrator.rest.ClarificationRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import feign.FeignException;
import java.util.Optional;
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

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public CnClarificationSavePeriod(final ClarificationRestClient clarificationRestClient,
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
        LOG.info("->Data preparation for E-Clarification.");
//        final String txId = execution.getProcessBusinessKey();
//        final Optional<OperationStepEntity> entityOptional = operationService.getLastOperation(txId);
//        if (entityOptional.isPresent()) {
//            LOG.info("->Send data to E-Clarification.");
//            final OperationStepEntity entity = entityOptional.get();
//            final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
//            final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
//            final String cpId = getCpId(jsonData);
//            final String startDate = getStartDate(jsonData);
//            final String endDate = getEndDate(jsonData);
//            try {
//                final ResponseEntity<ResponseDto> responseEntity = clarificationRestClient.postSavePeriod(
//                        cpId,
//                        params.getCountry(),
//                        params.getPmd(),
//                        "ps",
//                        params.getOwner(),
//                        startDate,
//                        endDate);
//                JsonNode responseData = jsonUtil.toJsonNode(responseEntity.getBody().getData());
//                operationService.processResponse(entity, addEnquiryPeriod(jsonData, responseData));
//            } catch (FeignException e) {
//                LOG.error(e.getMessage());
//                processService.processHttpException(e.status(), e.getMessage(), execution.getProcessInstanceId());
//            } catch (Exception e) {
//                LOG.error(e.getMessage());
//                processService.processHttpException(0, e.getMessage(), execution.getProcessInstanceId());
//            }
//        }
    }

    private String getCpId(JsonNode jsonData) {
        return jsonData.get("ocid").asText();
    }

    private String getStartDate(JsonNode jsonData) {
        final JsonNode tenderNode = jsonData.get("tender");
        final JsonNode tenderPeriodNode = tenderNode.get("tenderPeriod");
        return tenderPeriodNode.get("startDate").asText();
    }

    private String getEndDate(JsonNode jsonData) {
        final JsonNode tenderNode = jsonData.get("tender");
        final JsonNode tenderPeriodNode = tenderNode.get("tenderPeriod");
        return tenderPeriodNode.get("endDate").asText();
    }

    private JsonNode addEnquiryPeriod(JsonNode jsonData, JsonNode responseData) {
        final JsonNode tenderNode = jsonData.get("tender");
        ObjectNode enquiryPeriodNode = ((ObjectNode) tenderNode).putObject("enquiryPeriod");
        enquiryPeriodNode
                .put("startDate", responseData.get("startDate").asText())
                .put("endDate", responseData.get("endDate").asText());
        return jsonData;
    }
}
