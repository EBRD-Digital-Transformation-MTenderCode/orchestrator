package com.procurement.orchestrator.delegate.bpe;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.domain.enums.AwardCriteriaDetails;
import com.procurement.orchestrator.domain.enums.AwardStatusDetails;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BpeAnalyzeAwardingFlow implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(BpeAnalyzeAwardingFlow.class);
    private static final String BID_OPEN_DOCS = "bidOpenDocs";

    private final ProcessService processService;
    private final OperationService operationService;
    private final JsonUtil jsonUtil;

    public BpeAnalyzeAwardingFlow(
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
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        final String processId = execution.getProcessInstanceId();

        final Boolean bidOpenDocs = isBidOpenDocs(jsonData, processId);
        if (bidOpenDocs != null) {
            execution.setVariable(BID_OPEN_DOCS, bidOpenDocs);
            final String activityId = execution.getCurrentActivityId();
            LOG.debug("ACTIVITY ({}) IN CONTEXT PUT THE VARIABLE '{}' WITH THE VALUE '{}'.", activityId, BID_OPEN_DOCS, bidOpenDocs);
        }
    }

    private Boolean isBidOpenDocs(final JsonNode jsonData, final String processId) {
        try {
            final AwardCriteriaDetails awardCriteriaDetails = AwardCriteriaDetails.fromString(jsonData.get("awardCriteriaDetails").asText());
            final JsonNode nextAwardForUpdate = jsonData.get("nextAwardForUpdate");

            final boolean isAutomated = awardCriteriaDetails == AwardCriteriaDetails.AUTOMATED;
            final boolean isNextAwardPresent = nextAwardForUpdate != null;
            final boolean isNextAwardInStatusDetailsAwaiting;

            if (isNextAwardPresent) {
                final AwardStatusDetails awardStatusDetails = AwardStatusDetails.fromString(nextAwardForUpdate.get("statusDetails").asText());
                isNextAwardInStatusDetailsAwaiting = awardStatusDetails == AwardStatusDetails.AWAITING;
            } else {
                isNextAwardInStatusDetailsAwaiting = false;
            }

            return isAutomated && isNextAwardPresent && isNextAwardInStatusDetailsAwaiting;
        } catch (Exception e) {
            processService.terminateProcess(processId, e.getMessage());
            return null;
        }
    }


}
