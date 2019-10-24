package com.procurement.orchestrator.delegate.bpe;

import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BpeSetIsAuction implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(BpeSetIsAuction.class);
    private static final String VARIABLE_NAME = "isAuction";

    private final OperationService operationService;
    private final JsonUtil jsonUtil;

    public BpeSetIsAuction(final OperationService operationService, final JsonUtil jsonUtil) {
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        final String activityId = execution.getCurrentActivityId();
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());

        final boolean isAuction = context.getIsAuction();
        execution.setVariable(VARIABLE_NAME, isAuction);
        LOG.debug("ACTIVITY ({}) IN CONTEXT PUT THE VARIABLE '{}' WITH THE VALUE '{}'.", activityId, VARIABLE_NAME, isAuction);
    }
}
