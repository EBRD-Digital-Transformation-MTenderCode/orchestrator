package com.procurement.orchestrator.delegate.bpe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.domain.enums.AwardCriteria;
import com.procurement.orchestrator.domain.enums.AwardCriteriaDetails;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BpeSetAwardCriteriaDetails implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(BpeSetAwardCriteriaDetails.class);

    private final ProcessService processService;
    private final OperationService operationService;
    private final JsonUtil jsonUtil;

    public BpeSetAwardCriteriaDetails(
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
        final JsonNode requestData = jsonUtil.toJsonNode(entity.getResponseData());

        final String processId = execution.getProcessInstanceId();

        final JsonNode step = setAwardCriteriaDetails(requestData, processId);
        if (LOG.isDebugEnabled())
            LOG.debug("STEP FOR SAVE ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(step));

        operationService.saveOperationStep(execution, entity, context, step);

    }


    private JsonNode setAwardCriteriaDetails(final JsonNode jsonData, final String processId) {
        try {
            final AwardCriteria awardCriteria = AwardCriteria.fromString(jsonData.get("awardCriteria").asText());

            switch (awardCriteria) {
                case PRICE_ONLY:
                    ((ObjectNode) jsonData).replace("awardCriteriaDetails", jsonUtil.toJsonNode(AwardCriteriaDetails.AUTOMATED));
                case COST_ONLY:
                case QUALITY_ONLY:
                case RATED_CRITERIA:
                    ((ObjectNode) jsonData).replace("awardCriteriaDetails", jsonUtil.toJsonNode(AwardCriteriaDetails.MANUAL));
            }
            return jsonData;
        } catch (Exception e) {
            processService.terminateProcess(processId, e.getMessage());
            return null;
        }
    }

}
