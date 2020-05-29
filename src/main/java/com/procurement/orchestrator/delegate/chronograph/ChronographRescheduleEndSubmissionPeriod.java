package com.procurement.orchestrator.delegate.chronograph;

import java.time.LocalDateTime;
import java.util.UUID;
import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.delegate.kafka.MessageProducer;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.chronograph.ActionType;
import com.procurement.orchestrator.domain.chronograph.ScheduleTask;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ChronographRescheduleEndSubmissionPeriod implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ChronographRescheduleEndSubmissionPeriod.class);

    private static final String PROCESS_TYPE = "submissionPeriodEnd";
    private static final String PHASE = "submission";

    private final MessageProducer messageProducer;
    private final ProcessService processService;
    private final OperationService operationService;
    private final JsonUtil jsonUtil;
    private final DateUtil dateUtil;

    public ChronographRescheduleEndSubmissionPeriod(
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

        ChronographScheduleEndSubmissionPeriod.Metadata.MetadataBuilder metadataBuilder =
            ChronographScheduleEndSubmissionPeriod.Metadata
                .builder();

        final String uuid = UUID.randomUUID().toString();
        metadataBuilder.requestId(uuid);
        metadataBuilder.operationId(uuid);
        metadataBuilder.cpid(context.getCpid());
        metadataBuilder.ocid(context.getOcid());
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

        final ScheduleTask cancelTask = new ScheduleTask(
            ActionType.CANCEL,
            context.getCpid(),
            PHASE,
            null,
            null,
            jsonUtil.toJsonOrEmpty(null)
        );
        messageProducer.sendToChronograph(cancelTask);

        final ScheduleTask scheduleTask = new ScheduleTask(
            ActionType.SCHEDULE,
            context.getCpid(),
            PHASE,
            launchTime,
            null,
            jsonUtil.toJson(metadataBuilder.build())
        );
        messageProducer.sendToChronograph(scheduleTask);
        operationService.saveOperationStep(execution, entity, jsonUtil.toJsonNode(scheduleTask));
    }
}
