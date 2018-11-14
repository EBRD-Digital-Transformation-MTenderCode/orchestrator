package com.procurement.orchestrator.delegate.context;

import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Outcome;
import com.procurement.orchestrator.domain.Stage;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SaveContractContext implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(SaveContractContext.class);
    private final OperationService operationService;
    private final JsonUtil jsonUtil;

    public SaveContractContext(final OperationService operationService,
                               final JsonUtil jsonUtil) {
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final Set<Outcome> contextOutcomes = context.getOutcomes();
        context.setPhase("awardedContractPreparation");
        operationService.saveContext(context);

        context.setStage(Stage.AC.value());
        context.setPhase("contractProject");
        for (final Outcome outcome : contextOutcomes) {
            if (outcome.getType().equals("ac")) {
                operationService.saveContractContext(outcome.getId(), context);
            }
        }

        operationService.saveOperationStep(execution, entity);
    }
}
