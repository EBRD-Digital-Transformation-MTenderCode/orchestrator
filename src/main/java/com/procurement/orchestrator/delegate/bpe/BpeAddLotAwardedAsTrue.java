package com.procurement.orchestrator.delegate.bpe;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BpeAddLotAwardedAsTrue implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(BpeAddLotAwardedAsTrue.class);
    private static final boolean lotAwarded = true;

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        final String activityId = execution.getCurrentActivityId();
        execution.setVariable("lotAwarded", lotAwarded);
        LOG.debug("ACTIVITY ({}) IN CONTEXT PUT THE VARIABLE 'lotAwarded' WITH THE VALUE '{}'.", activityId, lotAwarded);
    }
}