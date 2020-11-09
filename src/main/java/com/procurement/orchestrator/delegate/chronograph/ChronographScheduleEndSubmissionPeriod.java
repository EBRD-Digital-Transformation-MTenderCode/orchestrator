package com.procurement.orchestrator.delegate.chronograph;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.orchestrator.databinding.LocalDateTimeDeserializer;
import com.procurement.orchestrator.databinding.LocalDateTimeSerializer;
import com.procurement.orchestrator.delegate.kafka.MessageProducer;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.chronograph.ActionType;
import com.procurement.orchestrator.domain.chronograph.ScheduleTask;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import lombok.Builder;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.procurement.orchestrator.domain.OperationType.CREATE_CN_ON_PN;
import static com.procurement.orchestrator.domain.OperationType.CREATE_FE;

@Component
public class ChronographScheduleEndSubmissionPeriod implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ChronographScheduleEndSubmissionPeriod.class);

    private static final String PROCESS_TYPE = "submissionPeriodEnd";
    private static final String PHASE = "submission";

    private final MessageProducer messageProducer;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;
    private final DateUtil dateUtil;

    public ChronographScheduleEndSubmissionPeriod(
        final MessageProducer messageProducer,
        final OperationService operationService,
        final ProcessService processService,
        final JsonUtil jsonUtil,
        final DateUtil dateUtil
    ) {
        this.messageProducer = messageProducer;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityId());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());

        Metadata.MetadataBuilder metadataBuilder = Metadata.builder();

        final String uuid = UUID.randomUUID().toString();
        metadataBuilder.requestId(uuid);
        metadataBuilder.operationId(uuid);
        metadataBuilder.cpid(context.getCpid());
        metadataBuilder.ocid(getOcid(context));
        metadataBuilder.processType(PROCESS_TYPE);
        metadataBuilder.phase(PHASE);

        final String owner = context.getOwner();
        metadataBuilder.owner(owner);
        metadataBuilder.platformId(owner);

        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        final String processId = execution.getProcessInstanceId();
        final LocalDateTime launchTime = dateUtil.stringToLocal(
            processService.getPreQualificationPeriodEndDate(jsonData, processId)
        );
        metadataBuilder.timestamp(launchTime);

        final ScheduleTask task = new ScheduleTask(
            ActionType.SCHEDULE,
            context.getCpid(),
            PHASE,
            launchTime,
            null,
            jsonUtil.toJson(metadataBuilder.build())
        );
        messageProducer.sendToChronograph(task);
        operationService.saveOperationStep(execution, entity, jsonUtil.toJsonNode(task));
    }

    private String getOcid(final Context context) {
        final String operationType = context.getOperationType();
        if (operationType.equals(CREATE_CN_ON_PN.toString())
                || operationType.equals(CREATE_FE.toString())) {
            return context.getOcidCn();
        } else {
            return context.getOcid();
        }
    }

    @Builder
    static class Metadata {
        @JsonProperty(value = "operationId")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String operationId;

        @JsonProperty(value = "requestId")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String requestId;

        @JsonProperty(value = "cpid")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String cpid;

        @JsonProperty(value = "ocid")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String ocid;

        @JsonProperty(value = "processType")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String processType;

        @JsonProperty(value = "phase")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String phase;

        @JsonProperty(value = "owner")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String owner;

        @JsonProperty(value = "platformId")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String platformId;

        @JsonProperty(value = "timestamp")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        private LocalDateTime timestamp;

        @JsonProperty(value = "isAuction")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Boolean isAuction = false;
    }
}
