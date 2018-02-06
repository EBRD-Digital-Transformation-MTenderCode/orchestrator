package com.procurement.orchestrator.delegate.tender.access;

import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
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
        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        params.setEndDate(dateUtil.format(dateUtil.localDateTimeNowUTC().plusSeconds(5)));
        final String processId = execution.getProcessInstanceId();
        try {
            operationService.saveOperationStep(
                    execution,
                    entity,
                    params);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            processService.processException(e.getMessage(), processId);
        }
    }
}
