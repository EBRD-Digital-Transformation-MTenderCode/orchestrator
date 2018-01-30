package com.procurement.orchestrator.delegate.cn.create;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.rest.SubmissionRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
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
        LOG.info(execution.getCurrentActivityName());
        final Optional<OperationStepEntity> entityOptional = operationService.getPreviousOperationStep(execution);
        if (entityOptional.isPresent()) {
            final OperationStepEntity entity = entityOptional.get();
            final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
            final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
            try {
                processService.processResponse(
                        submissionRestClient.savePeriod(
                                params.getCpid(),
                                "ps",
                                getStartDate(jsonData),
                                getEndDate(jsonData)),
                        execution.getProcessInstanceId(),
                        params.getOperationId());
                operationService.saveOperationStep(execution, entity);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                processService.processException(e.getMessage(), execution.getProcessInstanceId());
            }
        }
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
