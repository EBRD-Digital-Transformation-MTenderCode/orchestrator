package com.procurement.orchestrator.delegate.context;

import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Stage;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.RequestService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SaveContractActivationContext implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(SaveContractActivationContext.class);
    private final OperationService operationService;
    private final RequestService requestService;
    private final JsonUtil jsonUtil;

    public SaveContractActivationContext(final OperationService operationService,
                                         final RequestService requestService,
                                         final JsonUtil jsonUtil) {
        this.operationService = operationService;
        this.requestService = requestService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityId());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        operationService.saveContractContext(context.getOcid(), context);
        final Boolean stageEnd = (Boolean) execution.getVariable("stageEnd");
        if (stageEnd) {
            final Context evContext = requestService.getContext(context.getCpid());
            evContext.setPhase("empty");
            operationService.saveContext(evContext);
        }
        operationService.saveOperationStep(execution, entity, context);
    }
}
