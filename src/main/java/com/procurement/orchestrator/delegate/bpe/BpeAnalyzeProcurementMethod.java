package com.procurement.orchestrator.delegate.bpe;

import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BpeAnalyzeProcurementMethod implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(BpeAnalyzeProcurementMethod.class);

    private final ProcessService processService;
    private final OperationService operationService;
    private final JsonUtil jsonUtil;

    public BpeAnalyzeProcurementMethod(
        final ProcessService processService,
        final OperationService operationService,
        final JsonUtil jsonUtil
    ) {
        this.processService = processService;
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityId());

        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());

        final String processId = execution.getProcessInstanceId();
        final String operationId = context.getOperationId();
        final String pmd = context.getPmd();
        final Optional<Boolean> bidStatusChange = getBidStatusChange(pmd);
        if (bidStatusChange.isPresent()) {
            execution.setVariable("bidStatusChange", bidStatusChange);
            if (LOG.isDebugEnabled())
                LOG.debug("COMMAND ({}) IN CONTEXT PUT THE VARIABLE 'bidStatusChange' WITH THE VALUE '{}'.", operationId, bidStatusChange);
        } else {
            LOG.error("COMMAND ({}): Unknown pmd: '{}'.", operationId, pmd);
            processService.terminateProcess(processId, "Unknown pmd: '" + pmd + "'.");
        }
    }

    private Optional<Boolean> getBidStatusChange(final String pmd) {
        switch (pmd) {
            case "OT":
            case "OT_TEST":
            case "SV":
            case "SV_TEST":
            case "MV":
            case "MV_TEST":
                return Optional.of(true);
            case "DA":
            case "DA_TEST":
            case "NP":
            case "NP_TEST":
            case "OP":
            case "OP_TEST":
                return Optional.of(false);
            default:
                return Optional.empty();
        }
    }
}
