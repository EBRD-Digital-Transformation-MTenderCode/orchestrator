package com.procurement.orchestrator.delegate.clarification;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.ClarificationRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ClarificationCreateEnquiry implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ClarificationCreateEnquiry.class);

    private final ClarificationRestClient clarificationRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;
    private final DateUtil dateUtil;

    public ClarificationCreateEnquiry(final ClarificationRestClient clarificationRestClient,
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
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final JsonNode requestData = jsonUtil.toJsonNode(entity.getResponseData());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();
        final JsonNode responseData = processService.processResponse(
                clarificationRestClient.createEnquiry(
                        context.getCpid(),
                        context.getStage(),
                        context.getOwner(),
                        context.getStartDate(),
                        requestData),
                context,
                processId,
                taskId,
                requestData);
        if (Objects.nonNull(responseData))
            operationService.saveOperationStep(
                    execution,
                    entity,
                    addDataToContext(context, responseData, processId),
                    requestData,
                    responseData);
    }

    private Context addDataToContext(final Context context, final JsonNode responseData, final String processId) {
        return processService.addAccessToContext(context, "enquiry", null, responseData, processId);
    }
}
