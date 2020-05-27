package com.procurement.orchestrator.delegate.dossier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.DossierRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.procurement.orchestrator.domain.commands.DossierCommandType.VALIDATE_PERIOD;

@Component
public class DossierValidatePeriod implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(DossierValidatePeriod.class);

    private final DossierRestClient dossierRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public DossierValidatePeriod(final DossierRestClient dossierRestClient,
                                 final OperationService operationService,
                                 final ProcessService processService,
                                 final JsonUtil jsonUtil) {
        this.dossierRestClient = dossierRestClient;
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

        final JsonNode qualificationPeriod = getQualificationPeriod(jsonData, processId);
        final JsonNode commandMessage = processService.getCommandMessage(VALIDATE_PERIOD, context, qualificationPeriod);
        LOG.debug("COMMAND ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(commandMessage));

        final ResponseEntity<ResponseDto> response = dossierRestClient.execute(commandMessage);
        LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", context.getOperationId(), jsonUtil.toJson(response.getBody()));

        final JsonNode responseData =
            processService.processResponse(response, context, processId, taskId, commandMessage);
        LOG.debug(
            "RESPONSE AFTER PROCESSING ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(responseData)
        );

        if (responseData != null) {
            operationService.saveOperationStep(execution, entity, context, commandMessage, jsonData);
        }
    }

    private JsonNode getQualificationPeriod(JsonNode jsonData, String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.replace("period", jsonData.get("preQualification").get("period"));
            return mainNode;
        } catch (Exception e) {
            if (Objects.nonNull(processId)) processService.terminateProcess(processId, e.getMessage());
            return null;
        }
    }
}
