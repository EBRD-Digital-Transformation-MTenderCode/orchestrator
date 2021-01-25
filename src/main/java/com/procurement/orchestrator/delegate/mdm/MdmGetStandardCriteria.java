package com.procurement.orchestrator.delegate.mdm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.exception.OperationException;
import com.procurement.orchestrator.rest.MdmRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class MdmGetStandardCriteria implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(MdmGetStandardCriteria.class);
    private final MdmRestClient mdmRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public MdmGetStandardCriteria(final MdmRestClient mdmRestClient,
                                  final OperationService operationService,
                                  final ProcessService processService,
                                  final JsonUtil jsonUtil) {
        this.mdmRestClient = mdmRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityId());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final JsonNode prevData = jsonUtil.toJsonNode(entity.getResponseData());
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final String processId = execution.getProcessInstanceId();
        try {
            final String mainProcurementCategory = prevData.get("tender").get("mainProcurementCategory").asText();
            final ArrayNode responseCriteria = getStandardCriteria(context, mainProcurementCategory);
            final JsonNode updatedData = addStandardCriteria(prevData, responseCriteria);
            if (LOG.isDebugEnabled())
                LOG.debug("STEP FOR SAVE ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(updatedData));

            operationService.saveOperationStep(execution, entity, prevData, updatedData);
        } catch (Exception e) {
            processService.terminateProcess(processId, e.getMessage());
        }
    }

    private ArrayNode getStandardCriteria(Context context, String mainProcurementCategory) {
        LOG.debug("Get standard criteria by country: {}, lang: {}, and mainProcurementCategory: {}.", context.getCountry(), context.getLanguage(), mainProcurementCategory);
        ResponseEntity<String> response = mdmRestClient.getStandardCriteria(context.getCountry(), context.getLanguage(), mainProcurementCategory, null);
        final String criteriaData = response.getBody();
        LOG.debug("Received standard criteria by country: {}, lang: {}, and mainProcurementCategory: {} - '{}'.", context.getCountry(), context.getLanguage(), mainProcurementCategory, criteriaData);
        return (ArrayNode) jsonUtil.toJsonNode(criteriaData).get("data");
    }

    public JsonNode addStandardCriteria(JsonNode jsonData, ArrayNode responseCriteria) {
        if (responseCriteria.size() == 0)
            throw new OperationException("No criteria found.");
        final ArrayNode criteria = jsonUtil.createArrayNode();
        for (JsonNode responseCriterion : responseCriteria) {
            ObjectNode criterion = jsonUtil.createObjectNode();
            criterion.set("id", responseCriterion.get("id"));
            criterion.set("classification", responseCriterion.get("classification"));
            criteria.add(criterion);
        }
        ObjectNode mainNode = (ObjectNode) jsonData;
        mainNode.set("criteria", criteria);
        return jsonData;
    }
}
