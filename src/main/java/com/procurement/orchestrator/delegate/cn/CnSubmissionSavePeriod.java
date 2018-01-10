package com.procurement.orchestrator.delegate.cn;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.OperationEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.domain.constant.ResponseMessageType;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import com.procurement.orchestrator.rest.SubmissionRestClient;
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
public class CnSubmissionSavePeriod implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CnSubmissionSavePeriod.class);

    private final SubmissionRestClient submissionRestClient;

    private final OperationService operationService;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;

    public CnSubmissionSavePeriod(final SubmissionRestClient submissionRestClient,
                                  final OperationService operationService,
                                  final JsonUtil jsonUtil,
                                  final DateUtil dateUtil) {
        this.submissionRestClient = submissionRestClient;
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info("->Data preparation for E-Submission.");
        final String txId = execution.getProcessBusinessKey();
        final Optional<OperationEntity> entityOptional = operationService.getLastOperation(txId);
        if (entityOptional.isPresent()) {
            LOG.info("->Send data to E-Submission.");
            final OperationEntity entity = entityOptional.get();
            final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
            final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
            final String cpId = jsonData.get("ocid").asText();
            final JsonNode tenderNode = getTenderPeriod(jsonData);
            final String startDate = tenderNode.get("startDate").asText();
            final String endDate = tenderNode.get("endDate").asText();
            try {
                final ResponseEntity<ResponseDto> responseEntity = submissionRestClient.postSavePeriod(
                        cpId,
                        "ps",
                        startDate,
                        endDate);
                LOG.info("->Get response: " + responseEntity.getBody().getData());
                operationService.saveOperation(getEntity(entity));
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

    private OperationEntity getEntity(OperationEntity entity) {
        entity.setDate(dateUtil.getNowUTC());
        return entity;
    }

}
