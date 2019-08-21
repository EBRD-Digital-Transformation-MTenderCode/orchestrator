package com.procurement.orchestrator.delegate.contracting;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Outcome;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.ContractingRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static com.procurement.orchestrator.domain.commands.ContractingCommandType.CREATE_AC;

@Component
public class ContractingCreateAc implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ContractingCreateAc.class);

    private final ContractingRestClient contractingRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public ContractingCreateAc(
        final ContractingRestClient contractingRestClient,
        final OperationService operationService,
        final ProcessService processService,
        final JsonUtil jsonUtil
    ) {
        this.contractingRestClient = contractingRestClient;
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

        final JsonNode commandMessage = processService.getCommandMessage(CREATE_AC, context, jsonData);
        if (LOG.isDebugEnabled())
            LOG.debug("COMMAND ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(commandMessage));

        final ResponseEntity<ResponseDto> response = contractingRestClient.execute(commandMessage);
        if (LOG.isDebugEnabled())
            LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", context.getOperationId(), jsonUtil.toJson(response.getBody()));

        final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
        if (LOG.isDebugEnabled())
            LOG.debug("RESPONSE AFTER PROCESSING ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(responseData));

        if (responseData != null) {
            final Context updatedContext = addContractOutcomeToContext(context, responseData, processId);
            if (LOG.isDebugEnabled())
                LOG.debug("CONTEXT FOR SAVE (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(updatedContext) + "'.");

            final JsonNode step = processService.addCreateAcData(jsonData, responseData, processId);
            if (LOG.isDebugEnabled())
                LOG.debug("STEP FOR SAVE (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(step) + "'.");

            operationService.saveOperationStep(execution, entity, updatedContext, commandMessage, step);
        }
    }

    private Context addContractOutcomeToContext(final Context context, final JsonNode responseData, final String processId) {
        final JsonNode contractNode = responseData.get("contract");
        final String id = contractNode.get("id").asText();
        final String token = contractNode.get("token").asText();

        final Set<Outcome> outcomes;
        if (context.getOutcomes() != null) {
            outcomes = context.getOutcomes();
        } else {
            outcomes = new HashSet<>();
        }

        outcomes.add(new Outcome(id, token, "ac"));
        context.setOutcomes(outcomes);
        return context;
    }
}
