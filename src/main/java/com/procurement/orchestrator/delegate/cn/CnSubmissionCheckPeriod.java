package com.procurement.orchestrator.delegate.cn;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.cassandra.model.OperationEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.domain.constant.ResponseMessageType;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import com.procurement.orchestrator.rest.SubmissionRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CnSubmissionCheckPeriod implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CnSubmissionCheckPeriod.class);

    private final SubmissionRestClient submissionRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;

    public CnSubmissionCheckPeriod(final SubmissionRestClient submissionRestClient,
                                   final OperationService operationService,
                                   final ProcessService processService,
                                   final JsonUtil jsonUtil,
                                   final DateUtil dateUtil) {
        this.submissionRestClient = submissionRestClient;
        this.operationService = operationService;
        this.processService = processService;
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
            final LocalDateTime startDate = dateUtil.localDateTimeNowUTC();
            final LocalDateTime endDate = getPeriodEndDate(entity);
            HttpStatus httpStatus = null;
            try {
                final ResponseEntity<ResponseDto> responseEntity = submissionRestClient.postCheckPeriod(
                        params.getCountry(),
                        params.getPmd(),
                        "ps",
                        dateUtil.format(startDate),
                        dateUtil.format(endDate));
                httpStatus = responseEntity.getStatusCode();
                Map<String, Boolean> data = (HashMap) responseEntity.getBody().getData();
                if (!data.get("period")) {
                    throw new BpmnError("TR_EXCEPTION", ResponseMessageType.PERIOD_EXCEPTION.value());
                }
                operationService.processResponse(entity, params, addPeriodStartDate(entity, dateUtil.format(startDate)));
            } catch (Exception e) {
                LOG.error(e.getMessage());
                processService.processHttpException(httpStatus.is4xxClientError(), e.getMessage(),
                        execution.getProcessInstanceId());
            }
        }
    }

    private LocalDateTime getPeriodEndDate(OperationEntity entity) {
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
        final JsonNode tenderNode = jsonData.get("tender");
        final JsonNode tenderPeriodNode = tenderNode.get("tenderPeriod");
        final JsonNode endDateNode = tenderPeriodNode.get("endDate");
        return dateUtil.stringToLocal(endDateNode.asText());
    }

    private JsonNode addPeriodStartDate(OperationEntity entity, String startDate) {
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
        final JsonNode tenderNode = jsonData.get("tender");
        final JsonNode tenderPeriodNode = tenderNode.get("tenderPeriod");
        ((ObjectNode) tenderPeriodNode).put("startDate", startDate);
        return jsonData;
    }

}
