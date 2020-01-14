package com.procurement.orchestrator.delegate.contracting;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.ContractingRestClient;
import com.procurement.orchestrator.service.NotificationService;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import lombok.Getter;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.procurement.orchestrator.domain.commands.ContractingCommandType.TREASURY_RESPONSE_PROCESSING;

@Component
public class ContractingTreasuryProcessing implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ContractingTreasuryProcessing.class);

    private final ContractingRestClient contractingRestClient;
    private final NotificationService notificationService;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public ContractingTreasuryProcessing(final ContractingRestClient contractingRestClient,
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
        final String processId = execution.getProcessInstanceId();
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        final JsonNode commandData = generateCommandData(jsonData, processId);

        final String taskId = execution.getCurrentActivityId();
        final JsonNode commandMessage = processService.getCommandMessage(TREASURY_RESPONSE_PROCESSING, context, commandData);
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
                responseData);
        }
    }

    private JsonNode generateCommandData(final JsonNode jsonData, final String processId) {
        try {
            final JsonNode verificationNode = jsonData.get("verification");
            final TreasureAcDTO.Verification verification = new TreasureAcDTO.Verification(
                verificationNode.get("value").asText(),
                verificationNode.get("rationale").asText()
            );

            final String dateMet = jsonData.get("dateMet").asText();

            final TreasureAcDTO.RegData regData;
            if (jsonData.has("regData")) {
                final JsonNode regDataNode = jsonData.get("regData");
                final String externalRegId = regDataNode.get("externalRegId").asText();
                final String regDate = regDataNode.get("regDate").asText();
                regData = new TreasureAcDTO.RegData(externalRegId, regDate);
            } else {
                regData = null;
            }

            final TreasureAcDTO dto = new TreasureAcDTO(verification, dateMet, regData);
            return jsonUtil.toJsonNode(dto);

        } catch (Exception exception) {
            processService.terminateProcess(processId, exception.getMessage());
            return null;
        }
    }

    @Getter
    static class TreasureAcDTO {
        @JsonProperty("verification")
        private final Verification verification;

        @JsonProperty("dateMet")
        private final String dateMet;

        @JsonProperty("regData")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private final RegData regData;

        public TreasureAcDTO(final Verification verification, final String dateMet, final RegData regData) {
            Objects.requireNonNull(verification);
            Objects.requireNonNull(dateMet);

            this.verification = verification;
            this.dateMet = dateMet;
            this.regData = regData;
        }

        @Getter
        public static class Verification {
            @JsonProperty("value")
            private final String value;

            @JsonProperty("rationale")
            private final String rationale;

            public Verification(final String value, final String rationale) {
                Objects.requireNonNull(value);
                Objects.requireNonNull(rationale);

                this.value = value;
                this.rationale = rationale;
            }
        }

        @Getter
        public static class RegData {
            @JsonProperty("externalRegId")
            private final String externalRegId;

            @JsonProperty("regDate")
            private final String regDate;

            public RegData(final String externalRegId, final String regDate) {
                Objects.requireNonNull(externalRegId);
                Objects.requireNonNull(regDate);

                this.externalRegId = externalRegId;
                this.regDate = regDate;
            }
        }
    }
}
