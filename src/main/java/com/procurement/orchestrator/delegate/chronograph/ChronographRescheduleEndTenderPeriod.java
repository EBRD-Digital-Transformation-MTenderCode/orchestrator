package com.procurement.orchestrator.delegate.chronograph;

import com.datastax.driver.core.utils.UUIDs;
import com.procurement.orchestrator.config.kafka.MessageProducer;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Stage;
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
    private static final String TENDER_PERIOD_END = "tenderPeriodEnd";
    private static final String TENDER_PERIOD_END_EV = "tenderPeriodEndEv";
    private static final String NEXT_PHASE = "AWARDPERIOD";
    private static final String CURRENT_PHASE = "TENDERPERIOD";

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
        final Context contextForChronograph = new Context();
        contextForChronograph.setOperationId(UUIDs.timeBased().toString());
        if (context.getStage().equals(Stage.EV.value())) {
            contextForChronograph.setProcessType(TENDER_PERIOD_END_EV);
            contextForChronograph.setOperationType(TENDER_PERIOD_END_EV);
        } else {
            contextForChronograph.setProcessType(TENDER_PERIOD_END);
            contextForChronograph.setOperationType(TENDER_PERIOD_END);
        }
        contextForChronograph.setPhase(NEXT_PHASE);
        contextForChronograph.setCpid(context.getCpid());
        contextForChronograph.setStage(context.getStage());
        contextForChronograph.setOwner(context.getOwner());
        contextForChronograph.setCountry(context.getCountry());
        contextForChronograph.setPmd(context.getPmd());
        contextForChronograph.setStartDate(context.getStartDate());
        contextForChronograph.setEndDate(context.getEndDate());

        final ScheduleTask task = new ScheduleTask(
                ActionType.REPLACE,
                context.getCpid(),
                CURRENT_PHASE,
                null,
                dateUtil.stringToLocal(context.getEndDate()),
                jsonUtil.toJson(contextForChronograph));

        messageProducer.sendToChronograph(task);
        operationService.saveOperationStep(execution, entity, jsonUtil.toJsonNode(task));
    }
}
