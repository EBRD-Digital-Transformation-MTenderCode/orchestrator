package com.procurement.orchestrator.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SetDateForStage implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(SetDateForStage.class);

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;

    public SetDateForStage(final OperationService operationService,
                           final ProcessService processService,
                           final JsonUtil jsonUtil,
                           final DateUtil dateUtil) {
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
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
        final String processId = execution.getProcessInstanceId();
        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        params.setEndDate(processService.getTenderPeriodEndDate(jsonData, processId));
        operationService.saveOperationStep(
                execution,
                entity,
                params);
    }
}
