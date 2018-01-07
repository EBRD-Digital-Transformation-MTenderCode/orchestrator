package com.procurement.orchestrator.delegate.cn;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.OperationEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.constant.ResponseMessageType;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import com.procurement.orchestrator.domain.dto.cn.RequestSubmissionPeriodDto;
import com.procurement.orchestrator.domain.dto.cn.TenderPeriodDto;
import com.procurement.orchestrator.rest.SubmissionRestClient;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class CnSubmissionCheckPeriod implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CnSubmissionCheckPeriod.class);

    private final SubmissionRestClient submissionRestClient;

    private final OperationService operationService;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;

    public CnSubmissionCheckPeriod(final SubmissionRestClient submissionRestClient,
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
//        final String transactionId = execution.getProcessBusinessKey();
//        final Optional<OperationEntity> entityOptional = operationService.getOperationByStep(transactionId, 1);
//        if (entityOptional.isPresent()) {
//            LOG.info("->Send data to E-Submission.");
//            final OperationEntity entity = entityOptional.get();
//            final ResponseDto response;
//            try {
//                RequestSubmissionPeriodDto submissionPeriodDto = getRequestSubmissionPeriodDto(entity);
//                final ResponseEntity<ResponseDto> responseEntity = submissionRestClient.postCheckPeriod(submissionPeriodDto);
//                response = responseEntity.getBody();
//                LOG.info("->Get response: " + response.getData());
//                if (!response.getSuccess()) {
//                    throw new BpmnError("TR_EXCEPTION", ResponseMessageType.PERIOD_EXCEPTION.value());
//                }
//            } catch (Exception e) {
//                LOG.error(e.getMessage());
//                throw new BpmnError("TR_EXCEPTION", ResponseMessageType.SERVICE_EXCEPTION.value());
//            }
//
//            final OperationValue operation = new OperationValue(
//                    transactionId,
//                    2,
//                    "check period in submission",
//                    "e-submission",
//                    "orchestrator",
//                    entity.getProcessType(),
//                    jsonUtil.toJson(response.getData()));
//
//            operationService.saveOperation(operation);
//        }
    }

//    private RequestSubmissionPeriodDto getRequestSubmissionPeriodDto(OperationEntity entity) throws Exception {
//        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
//        final JsonNode tenderNode = jsonData.get("tender");
//        final JsonNode tenderPeriodNode = tenderNode.get("tenderPeriod");
//        final JsonNode endDateNode = tenderNode.get("endDate");
//        final LocalDateTime startDate = dateUtil.getNowUTC();
//        final LocalDateTime endDate = dateUtil.stringToLocalDateTime(endDateNode.asText());
//        return new RequestSubmissionPeriodDto(null,
//                jsonData.get("country").asText(),
//                jsonData.get("procurementMethodDetails").asText(),
//                new TenderPeriodDto(startDate, endDate));
//    }

}
