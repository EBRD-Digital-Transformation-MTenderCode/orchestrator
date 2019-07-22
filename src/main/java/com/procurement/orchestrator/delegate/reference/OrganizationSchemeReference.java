package com.procurement.orchestrator.delegate.reference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.MdmRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class OrganizationSchemeReference implements JavaDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(OrganizationSchemeReference.class);

    private final MdmRestClient mdmRestClient;
    private final OperationService operationService;
    private final JsonUtil jsonUtil;

    public OrganizationSchemeReference(
        final MdmRestClient mdmRestClient,
        final OperationService operationService,
        final JsonUtil jsonUtil
    ) {
        this.mdmRestClient = mdmRestClient;
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityId());

        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final JsonNode updatedData = jsonUtil.toJsonNode(entity.getResponseData());

        final String countryId = context.getCountry();
        final JsonNode schemes = getOrganizationSchemesData(countryId);

        final ObjectNode mdm;
        if (updatedData.has("mdm"))
            mdm = (ObjectNode) updatedData.get("mdm");
        else {
            mdm = jsonUtil.createObjectNode();
            ((ObjectNode) updatedData).set("mdm", mdm);
        }
        mdm.set("schemes", schemes);

        final JsonNode prevData = jsonUtil.toJsonNode(entity.getResponseData());
        operationService.saveOperationStep(execution, entity, prevData, updatedData);
    }

    private JsonNode getOrganizationSchemesData(final String countryId) {
        LOG.debug("Get data of schemes by country: {}.", countryId);
        ResponseEntity<String> response = mdmRestClient.getOrganizationSchemes(countryId);
        final String schemesData = response.getBody();
        LOG.debug("Received data of schemes by country: {} - '{}'.", countryId, schemesData);
        return jsonUtil.toJsonNode(schemesData).get("data").get("schemes");
    }
}
