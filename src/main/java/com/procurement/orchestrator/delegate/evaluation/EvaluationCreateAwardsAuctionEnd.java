package com.procurement.orchestrator.delegate.evaluation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.EvaluationRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static com.procurement.orchestrator.domain.commands.EvaluationCommandType.CREATE_AWARDS_AUCTION_END;

@Component
public class EvaluationCreateAwardsAuctionEnd implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(EvaluationCreateAwardsAuctionEnd.class);

    private final EvaluationRestClient evaluationRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public EvaluationCreateAwardsAuctionEnd(
        final EvaluationRestClient evaluationRestClient,
        final OperationService operationService,
        final ProcessService processService,
        final JsonUtil jsonUtil
    ) {
        this.evaluationRestClient = evaluationRestClient;
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

        final JsonNode preparedData = setElectronicAuctionsToData(jsonData, processId);
        final JsonNode commandMessage = processService.getCommandMessage(CREATE_AWARDS_AUCTION_END, context, preparedData);
        LOG.debug("COMMAND ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(commandMessage));

        final ResponseEntity<ResponseDto> response = evaluationRestClient.execute(commandMessage);
        LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", context.getOperationId(), jsonUtil.toJson(response.getBody()));

        final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
        LOG.debug("RESPONSE AFTER PROCESSING ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(responseData));

        if (responseData != null) {
            operationService.saveOperationStep(execution, entity, context, commandMessage);
        }
    }

    private JsonNode setElectronicAuctionsToData(final JsonNode jsonData, final String processId) {
        try {
            final JsonNode tenderNode = jsonData.get("tender");
            final JsonNode electronicAuctions = tenderNode.get("electronicAuctions");
            ((ObjectNode) jsonData).set("electronicAuctions", electronicAuctions);
            return jsonData;
        } catch (Exception e) {
            processService.terminateProcess(processId, e.getMessage());
            return null;
        }
    }

}
