package com.procurement.orchestrator.delegate.clarification;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.ClarificationRestClient;
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

import static com.procurement.orchestrator.domain.commands.ClarificationCommandType.CREATE_PERIOD;

@Component
public class ClarificationCreatePeriod implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ClarificationCreatePeriod.class);

    private final ClarificationRestClient clarificationRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public ClarificationCreatePeriod(
        final ClarificationRestClient clarificationRestClient,
        final OperationService operationService,
        final ProcessService processService,
        final JsonUtil jsonUtil
    ) {
        this.clarificationRestClient = clarificationRestClient;
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

        final Context commandContext = new Context.Builder()
                .setCpid(context.getCpid())
                .setOcid(getOcid(context))
                .setOwner(context.getOwner())
                .setCountry(context.getCountry())
                .setPmd(context.getPmd())
                .build();

        final JsonNode period = getPeriod(jsonData, processId);
        final JsonNode commandMessage = processService.getCommandMessage(CREATE_PERIOD, commandContext, period);
        if (LOG.isDebugEnabled()) {
            LOG.debug("COMMAND ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(commandMessage));
        }

        final ResponseEntity<ResponseDto> response = clarificationRestClient.execute(commandMessage);
        if (LOG.isDebugEnabled()) {
            LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", context.getOperationId(), jsonUtil.toJson(response.getBody()));
        }

        final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
        if (LOG.isDebugEnabled()) {
            LOG.debug("RESPONSE AFTER PROCESSING ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(responseData));
        }

        if (responseData != null) {
            final JsonNode step = setEnquiryPeriod(jsonData, responseData, processId);
            if (LOG.isDebugEnabled()) {
                LOG.debug("STEP FOR SAVE ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(step));
            }

            operationService.saveOperationStep(execution, entity, commandMessage, step);
        }
    }

    private JsonNode getPeriod(JsonNode jsonData, String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.replace("period", jsonData.get("preQualification").get("period"));
            return mainNode;
        } catch (Exception e) {
            if (Objects.nonNull(processId)) processService.terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    private JsonNode setEnquiryPeriod(JsonNode jsonData, JsonNode respData, String processId) {
        try {
            final ObjectNode srcEnquiryPeriod = (ObjectNode) respData.get("enquiryPeriod");
            final ObjectNode tenderNode = (ObjectNode) jsonData.get("tender");

            final ObjectNode dstEnquiryPeriod = (tenderNode.has("enquiryPeriod"))
                    ? (ObjectNode) tenderNode.get("enquiryPeriod")
                    : jsonUtil.createObjectNode();

            dstEnquiryPeriod.set("startDate", srcEnquiryPeriod.get("startDate"));
            dstEnquiryPeriod.set("endDate", srcEnquiryPeriod.get("endDate"));

            tenderNode.set("enquiryPeriod", dstEnquiryPeriod);

            return jsonData;
        } catch (Exception e) {
            processService.terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public String getOcid(final Context context) {
        if (context.getOcidCn() != null)
            return context.getOcidCn();
        else
            return context.getOcid();
    }
}
