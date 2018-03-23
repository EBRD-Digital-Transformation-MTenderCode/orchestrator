package com.procurement.orchestrator.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.cassandra.service.OperationService;
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
        final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        params.setEndDate(dateUtil.format(dateUtil.localDateTimeNowUTC().plusSeconds(5)));
        params.setOperationType("standstillPeriodEnd");
        final String processId = execution.getProcessInstanceId();
        operationService.saveOperationStep(
                execution,
                entity,
                params,
                processService.addStandstillPeriod(
                        jsonData,
                        params.getStartDate(),
                        params.getEndDate(),
                        processId));
    }
}
