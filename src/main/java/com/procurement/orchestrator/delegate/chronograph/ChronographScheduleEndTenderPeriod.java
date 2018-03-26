
package com.procurement.orchestrator.delegate.chronograph;

import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.config.kafka.MessageProducer;
import com.procurement.orchestrator.domain.chronograph.ChronographTask;
import com.procurement.orchestrator.service.ProcessService;
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

    private final MessageProducer messageProducer;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;

    public ChronographScheduleEndTenderPeriod(final MessageProducer messageProducer,
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
        final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
        /**set params for next process*/
        final Params paramsForEndTenderPeriod = new Params();
        paramsForEndTenderPeriod.setProcessType("tenderPeriodEnd");
        paramsForEndTenderPeriod.setOperationType("tenderPeriodEnd");
        paramsForEndTenderPeriod.setCpid(params.getCpid());
        paramsForEndTenderPeriod.setStage(params.getStage());
        paramsForEndTenderPeriod.setOwner(params.getOwner());
        paramsForEndTenderPeriod.setCountry(params.getCountry());
        paramsForEndTenderPeriod.setPmd(params.getPmd());
        paramsForEndTenderPeriod.setStartDate(params.getStartDate());
        paramsForEndTenderPeriod.setEndDate(params.getEndDate());

        ChronographTask task = new ChronographTask(
                ChronographTask.ActionType.SCHEDULE,
                params.getCpid(),
                "tenderPeriodEnd",
                dateUtil.stringToLocal(params.getEndDate()),
                jsonUtil.toJson(paramsForEndTenderPeriod));
        messageProducer.sendToChronograph(task);
    }
}