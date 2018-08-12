package com.procurement.orchestrator.delegate.chronograph;

import com.datastax.driver.core.utils.UUIDs;
import com.procurement.orchestrator.config.kafka.MessageProducer;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.chronograph.ActionType;
import com.procurement.orchestrator.domain.chronograph.ScheduleTask;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ChronographRescheduleEndTenderPeriod implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ChronographRescheduleEndTenderPeriod.class);

    private final MessageProducer messageProducer;

    private final OperationService operationService;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;


    public ChronographRescheduleEndTenderPeriod(final MessageProducer messageProducer,
                                                final OperationService operationService,
                                                final JsonUtil jsonUtil,
                                                final DateUtil dateUtil) {
        this.messageProducer = messageProducer;
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        /**set context for next process*/
        final Context contextChronograph = new Context();
        final String uuid = UUIDs.timeBased().toString();
        contextChronograph.setCpid(context.getCpid());
        contextChronograph.setProcessType("tenderPeriodEnd");
        contextChronograph.setOperationId(uuid);
        contextChronograph.setRequestId(uuid);
        contextChronograph.setSetExtendedPeriod(null);

        final ScheduleTask task = new ScheduleTask(
                ActionType.REPLACE,
                context.getCpid(),
                context.getPhase(),
                dateUtil.stringToLocal(context.getEndDate()),
                dateUtil.stringToLocal(context.getEndDate()),
                jsonUtil.toJson(contextChronograph));
        messageProducer.sendToChronograph(task);
        operationService.saveOperationStep(execution, entity, jsonUtil.toJsonNode(task));
    }
}
