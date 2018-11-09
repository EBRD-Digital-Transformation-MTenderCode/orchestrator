package com.procurement.orchestrator.delegate.standstill;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Stage;
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
public class SetStandstillPeriod implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(SetStandstillPeriod.class);


    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;

    public SetStandstillPeriod(final DateUtil dateUtil,
                               final OperationService operationService,
                               final ProcessService processService,
                               final JsonUtil jsonUtil) {
        this.dateUtil = dateUtil;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        context.setEndDate(dateUtil.format(dateUtil.localDateTimeNowUTC().plusSeconds(5)));
        if (context.getStage().equals(Stage.EV.value())) {
            context.setOperationType("standstillPeriodEv");
        } else {
            context.setOperationType("standstillPeriod");
        }
        final String processId = execution.getProcessInstanceId();
        operationService.saveOperationStep(
                execution,
                entity,
                context,
                jsonData,
                processService.addStandstillPeriod(
                        jsonData,
                        context.getStartDate(),
                        context.getEndDate(),
                        processId));
    }
}
