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
public class BpeSetValuesInCamundaContext implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(BpeSetValuesInCamundaContext.class);

    private static final String ATTRIBUTE_PMD = "pmd";
    private static final String ATTRIBUTE_STAGE = "stage";

    private final OperationService operationService;
    private final JsonUtil jsonUtil;

    public BpeSetValuesInCamundaContext(
            final OperationService operationService,
            final JsonUtil jsonUtil
    ) {
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        final String activityId = execution.getCurrentActivityId();

        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());

        execution.setVariable(ATTRIBUTE_PMD, context.getPmd());
        if (LOG.isDebugEnabled())
            LOG.debug("COMMAND ({}) IN CONTEXT PUT THE VARIABLE '{}' WITH THE VALUE '{}'.", activityId, ATTRIBUTE_PMD, context.getPmd());

        execution.setVariable(ATTRIBUTE_STAGE, context.getStage());
        if (LOG.isDebugEnabled())
            LOG.debug("COMMAND ({}) IN CONTEXT PUT THE VARIABLE '{}' WITH THE VALUE '{}'.", activityId, ATTRIBUTE_STAGE, context.getStage());
    }
}
