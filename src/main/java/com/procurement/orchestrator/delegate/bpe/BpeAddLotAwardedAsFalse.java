package com.procurement.orchestrator.delegate.bpe;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BpeAddLotAwardedAsFalse implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(BpeAddLotAwardedAsFalse.class);
    private static final boolean lotAwarded = false;
    private static final String LOT_AWARDED = "lotAwarded";

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        final String activityId = execution.getCurrentActivityId();
        execution.setVariable(LOT_AWARDED, lotAwarded);
        LOG.debug("ACTIVITY ({}) IN CONTEXT PUT THE VARIABLE '{}' WITH THE VALUE '{}'.", activityId, LOT_AWARDED, lotAwarded);
    }
}
