package com.procurement.orchestrator.delegate.stage;

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
public class GetContext implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(GetContext.class);
    private final OperationService operationService;
    private final JsonUtil jsonUtil;

    public GetContext(final OperationService operationService,
                      final JsonUtil jsonUtil) {
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
//        LOG.info(execution.getCurrentActivityName());
//        final String processId = execution.getProcessInstanceId();
//        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
//        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
//        final Context prevContext = operationService.getContext(context.getCpid(), processId);
//        context.setCountry(prevContext.getCountry());
//        context.setLanguage(prevContext.getLanguage());
//        operationService.saveOperationStep(execution, entity, context);
    }
}
