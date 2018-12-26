package com.procurement.orchestrator.delegate.contracting;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.ContractingRestClient;
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

import static com.procurement.orchestrator.domain.commands.ContractingCommandType.UPDATE_AC;

@Component
public class ContractingUpdateAc implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ContractingUpdateAc.class);

    private final ContractingRestClient contractingRestClient;
    private final NotificationService notificationService;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public ContractingUpdateAc(final ContractingRestClient contractingRestClient,
                               final NotificationService notificationService,
                               final OperationService operationService,
                               final ProcessService processService,
                               final JsonUtil jsonUtil) {
        this.contractingRestClient = contractingRestClient;
        this.notificationService = notificationService;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityId());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();
        final JsonNode commandMessage = processService.getCommandMessage(UPDATE_AC, context, jsonData);
        JsonNode responseData = processService.processResponse(
                contractingRestClient.execute(commandMessage),
                context,
                processId,
                taskId,
                commandMessage);
        if (Objects.nonNull(responseData)) {
            operationService.saveOperationStep(
                    execution,
                    entity,
                    context,
                    commandMessage,
                    processService.setContractUpdateData(jsonData, responseData, processId));
        }
    }

}
