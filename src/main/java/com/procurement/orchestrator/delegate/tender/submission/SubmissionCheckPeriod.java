package com.procurement.orchestrator.delegate.tender.submission;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.rest.SubmissionRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Objects;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SubmissionCheckPeriod implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(SubmissionCheckPeriod.class);

    private final SubmissionRestClient submissionRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public SubmissionCheckPeriod(final SubmissionRestClient submissionRestClient,
                                 final OperationService operationService,
                                 final ProcessService processService,
                                 final JsonUtil jsonUtil) {
        this.submissionRestClient = submissionRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
        final String processId = execution.getProcessInstanceId();
        final String operationId = params.getOperationId();
        final String taskId = execution.getCurrentActivityId();
        final JsonNode responseData = processService.processResponse(
                submissionRestClient.checkPeriod(
                        params.getCpid(),
                        params.getCountry(),
                        params.getPmd(),
                        params.getStage(),
                        params.getStartDate(),
                        params.getEndDate()),
                processId,
                operationId,
                taskId);
        if (Objects.nonNull(responseData)) {
            operationService.saveOperationStep(execution, entity);
        }
    }
}
