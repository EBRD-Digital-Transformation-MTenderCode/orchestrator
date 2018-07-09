package com.procurement.orchestrator.delegate.chronograph;

import com.datastax.driver.core.utils.UUIDs;
import com.procurement.orchestrator.config.kafka.MessageProducer;
import com.procurement.orchestrator.domain.Params;
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
public class ChronographScheduleEndTenderPeriod implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ChronographScheduleEndTenderPeriod.class);
    private static final String TENDER_PERIOD_END = "tenderPeriodEnd";
    private static final String TENDER_PERIOD_END_EV = "tenderPeriodEndEv";
    private static final String NEXT_PHASE = "AWARDPERIOD";
    private static final String CURRENT_PHASE = "TENDERPERIOD";

    private final MessageProducer messageProducer;

    private final OperationService operationService;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;

    public ChronographScheduleEndTenderPeriod(final MessageProducer messageProducer,
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
        final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
        /**set params for next process*/
        final Params paramsForChronograph = new Params();
        paramsForChronograph.setOperationId(UUIDs.timeBased().toString());
        if (params.getNewStage().equals(Stage.EV.value())) {
            paramsForChronograph.setProcessType(TENDER_PERIOD_END_EV);
            paramsForChronograph.setOperationType(TENDER_PERIOD_END_EV);
        } else {
            paramsForChronograph.setProcessType(TENDER_PERIOD_END);
            paramsForChronograph.setOperationType(TENDER_PERIOD_END);
        }
        paramsForChronograph.setPhase(NEXT_PHASE);
        paramsForChronograph.setCpid(params.getCpid());
        paramsForChronograph.setNewStage(params.getNewStage());
        paramsForChronograph.setOwner(params.getOwner());
        paramsForChronograph.setCountry(params.getCountry());
        paramsForChronograph.setPmd(params.getPmd());
        paramsForChronograph.setStartDate(params.getStartDate());
        paramsForChronograph.setEndDate(params.getEndDate());

        final ScheduleTask task = new ScheduleTask(
                ActionType.SCHEDULE,
                params.getCpid(),
                CURRENT_PHASE,
                dateUtil.stringToLocal(params.getEndDate()),
                null,
                jsonUtil.toJson(paramsForChronograph));
        messageProducer.sendToChronograph(task);
        operationService.saveOperationStep(execution, entity, jsonUtil.toJsonNode(task));
    }
}
