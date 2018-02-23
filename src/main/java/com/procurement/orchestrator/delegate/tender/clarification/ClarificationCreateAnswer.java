package com.procurement.orchestrator.delegate.tender.clarification;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.cassandra.model.Params;
import com.procurement.orchestrator.rest.ClarificationRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Objects;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ClarificationCreateAnswer implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ClarificationCreateAnswer.class);

    private final ClarificationRestClient clarificationRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;
    private final DateUtil dateUtil;

    public ClarificationCreateAnswer(final ClarificationRestClient clarificationRestClient,
                                     final OperationService operationService,
                                     final ProcessService processService,
                                     final JsonUtil jsonUtil,
                                     final DateUtil dateUtil) {
        this.clarificationRestClient = clarificationRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
        final String processId = execution.getProcessInstanceId();
        final String operationId = params.getOperationId();
        final String taskId = execution.getCurrentActivityId();
        final JsonNode responseData = processService.processResponse(
                clarificationRestClient.updateEnquiry(
                        params.getCpid(),
                        params.getStage(),
                        params.getToken(),
                        params.getOwner(),
                        dateUtil.format(dateUtil.localDateTimeNowUTC()),
                        jsonData),
                processId,
                operationId,
                taskId);
        if (Objects.nonNull(responseData)) {
            final Boolean allAnswered = getAllAnswered(responseData, processId);
            execution.setVariable("allAnswered", (allAnswered ? 1 : 0));
            operationService.saveOperationStep(execution, entity, responseData);
        }
    }

    private Boolean getAllAnswered(final JsonNode responseData, final String processId) {
        return processService.getBoolean("allAnswered", responseData, processId);
    }
}
