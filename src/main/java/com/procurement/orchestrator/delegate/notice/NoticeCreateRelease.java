package com.procurement.orchestrator.delegate.notice;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.NoticeRestClient;
import com.procurement.orchestrator.service.NotificationService;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.procurement.orchestrator.domain.commands.NoticeCommandType.CREATE_RELEASE;

@Component
public class NoticeCreateRelease implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(NoticeCreateRelease.class);


    private final NotificationService notificationService;
    private final NoticeRestClient noticeRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public NoticeCreateRelease(final NotificationService notificationService,
                               final NoticeRestClient noticeRestClient,
                               final OperationService operationService,
                               final ProcessService processService,
                               final JsonUtil jsonUtil) {
        this.notificationService = notificationService;
        this.noticeRestClient = noticeRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final JsonNode requestData = jsonUtil.toJsonNode(entity.getResponseData());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();
        final JsonNode commandMessage = processService.getCommandMessage(CREATE_RELEASE, context, requestData);
        JsonNode responseData = null;
        if (Objects.nonNull(requestData)) {
            responseData = processService.processResponse(
                    noticeRestClient.execute(commandMessage),
                    context,
                    processId,
                    taskId,
                    commandMessage);
        }
        if (Objects.nonNull(responseData)) {
            operationService.saveOperationStep(
                    execution,
                    entity,
                    addDataToContext(context, responseData, processId),
                    commandMessage,
                    responseData);
        }
    }

    private Context addDataToContext(final Context context, final JsonNode responseData, final String processId) {
        return notificationService.addNoticeOutcomeToContext(context, responseData, processId);
    }
}
