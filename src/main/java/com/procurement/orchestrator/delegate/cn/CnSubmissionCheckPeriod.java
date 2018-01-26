package com.procurement.orchestrator.delegate.cn;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.domain.constant.ResponseMessageType;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import com.procurement.orchestrator.rest.SubmissionRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import feign.FeignException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        LOG.info(execution.getCurrentActivityName());
        final Optional<OperationStepEntity> entityOptional = operationService.getPreviousOperationStep(execution);
        if (entityOptional.isPresent()) {
            final OperationStepEntity entity = entityOptional.get();
            final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
            final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
            final String startDate = dateUtil.format(dateUtil.localDateTimeNowUTC());
            try {
                final ResponseEntity<ResponseDto> responseEntity = submissionRestClient.checkPeriod(
                        params.getCountry(),
                        params.getPmd(),
                        "ps",
                        startDate,
                        getEndDate(jsonData));
                Map<String, Boolean> data = (HashMap) responseEntity.getBody().getData();
                if (!data.get("period")) {
                    throw new BpmnError("TR_EXCEPTION", ResponseMessageType.PERIOD_EXCEPTION.value());
                }
                operationService.saveOperationStep(
                        execution,
                        addPeriodStartDate(entity, jsonData, startDate),
                        params);
            } catch (FeignException e) {
                LOG.error(e.getMessage(), e);
                processService.processHttpException(e.status(), e.getMessage(), execution.getProcessInstanceId());
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                processService.processHttpException(0, e.getMessage(), execution.getProcessInstanceId());
            }
        }
    }

    private String getEndDate(final JsonNode jsonData) {
        final JsonNode tenderNode = jsonData.get("tender");
        final JsonNode tenderPeriodNode = tenderNode.get("tenderPeriod");
        return tenderPeriodNode.get("endDate").asText();
    }

    private OperationStepEntity addPeriodStartDate(final OperationStepEntity entity,
                                                   final JsonNode jsonData,
                                                   final String startDate) {
        final JsonNode tenderNode = jsonData.get("tender");
        final JsonNode tenderPeriodNode = tenderNode.get("tenderPeriod");
        ((ObjectNode) tenderPeriodNode).put("startDate", startDate);
        entity.setJsonData(jsonUtil.toJson(jsonData));
        return entity;
    }

}
