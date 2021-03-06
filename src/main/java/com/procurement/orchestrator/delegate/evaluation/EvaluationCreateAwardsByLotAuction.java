package com.procurement.orchestrator.delegate.evaluation;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.EvaluationRestClient;
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

import static com.procurement.orchestrator.domain.commands.EvaluationCommandType.CREATE_AWARDS_BY_LOT_AUCTION;

@Component
public class EvaluationCreateAwardsByLotAuction implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(EvaluationCreateAwardsByLotAuction.class);

    private final EvaluationRestClient evaluationRestClient;
    private final NotificationService notificationService;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public EvaluationCreateAwardsByLotAuction(final EvaluationRestClient evaluationRestClient,
                                              final NotificationService notificationService,
                                              final OperationService operationService,
                                              final ProcessService processService,
                                              final JsonUtil jsonUtil) {
        this.evaluationRestClient = evaluationRestClient;
        this.notificationService = notificationService;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityId());
        final String processId = execution.getProcessInstanceId();
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final String taskId = execution.getCurrentActivityId();
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        final JsonNode commandMessage = processService.getCommandMessage(CREATE_AWARDS_BY_LOT_AUCTION, context, jsonData);
        final JsonNode responseData = processService.processResponse(
                evaluationRestClient.execute(commandMessage),
                context,
                processId,
                taskId,
                commandMessage);
        if (Objects.nonNull(responseData)) {
            operationService.saveOperationStep(
                    execution,
                    entity,
                    notificationService.addAwardOutcomeToContext(context, responseData, processId),
                    commandMessage,
                    processService.addAwardData(jsonData, responseData, processId));
        }
    }
}


