package com.procurement.orchestrator.delegate.cn;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.rest.SubmissionRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import feign.FeignException;
import java.util.Optional;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CnSubmissionSavePeriod implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CnSubmissionSavePeriod.class);

    private final SubmissionRestClient submissionRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;

    public CnSubmissionSavePeriod(final SubmissionRestClient submissionRestClient,
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
//        final String txId = execution.getProcessBusinessKey();
//        final Optional<OperationStepEntity> entityOptional = operationService.getLastOperation(txId);
//        if (entityOptional.isPresent()) {
//            LOG.info("->Send data to E-Submission.");
//            final OperationStepEntity entity = entityOptional.get();
//            final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
//            final String cpId = getCpId(jsonData);
//            final String startDate = getStartDate(jsonData);
//            final String endDate = getEndDate(jsonData);
//            try {
//                submissionRestClient.postSavePeriod(
//                        cpId,
//                        "ps",
//                        startDate,
//                        endDate);
//                operationService.processResponse(entity);
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
}
