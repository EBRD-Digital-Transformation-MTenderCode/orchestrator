package com.procurement.orchestrator.delegate.chronograph;

import com.datastax.driver.core.utils.UUIDs;
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

import java.time.LocalDateTime;

@Component
public class ChronographRescheduleEndTenderPeriod implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ChronographRescheduleEndTenderPeriod.class);

    private final MessageProducer messageProducer;
    private final ProcessService processService;
    private final OperationService operationService;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;


    public ChronographRescheduleEndTenderPeriod(final MessageProducer messageProducer,
                                                final OperationService operationService,
                                                final ProcessService processService,
                                                final JsonUtil jsonUtil,
                                                final DateUtil dateUtil) {
        this.messageProducer = messageProducer;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        /*set context for next process*/
        final Context contextChronograph = new Context();
        final String uuid = UUIDs.timeBased().toString();
        contextChronograph.setCpid(context.getCpid());
//        if (context.getIsAuction()) {
//            contextChronograph.setProcessType("tenderPeriodEndAuction");
//        } else {
            contextChronograph.setProcessType("tenderPeriodEnd");
//        }
        contextChronograph.setOperationId(uuid);
        contextChronograph.setRequestId(uuid);

        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        final String processId = execution.getProcessInstanceId();
        final LocalDateTime newLaunchTime = dateUtil.stringToLocal(
                processService.getTenderPeriodEndDate(jsonData, processId));

        final ScheduleTask cancelTask = new ScheduleTask(
                ActionType.CANCEL,
                context.getCpid(),
                "tendering",
                null,
                null,
                jsonUtil.toJson(contextChronograph));
        messageProducer.sendToChronograph(cancelTask);

        final ScheduleTask scheduleTask = new ScheduleTask(
                ActionType.SCHEDULE,
                context.getCpid(),
                "tendering",
                newLaunchTime, /*launchTime*/
                null,
                jsonUtil.toJson(contextChronograph));
        messageProducer.sendToChronograph(scheduleTask);
        operationService.saveOperationStep(execution, entity, jsonUtil.toJsonNode(scheduleTask));
    }
}
