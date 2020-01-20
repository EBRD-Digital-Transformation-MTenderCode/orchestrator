package com.procurement.orchestrator.delegate.reference;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.MdmRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.utils.JsonUtil;
import lombok.Getter;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class OrganizationSchemeForSupplier implements JavaDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(OrganizationSchemeForSupplier.class);

    private final MdmRestClient mdmRestClient;
    private final OperationService operationService;
    private final JsonUtil jsonUtil;

    public OrganizationSchemeForSupplier(
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
        final JsonNode updatedData = jsonUtil.toJsonNode(entity.getResponseData());

        final ArrayNode suppliers = (ArrayNode) (updatedData.get("award").get("suppliers"));
        final List<String> countriesIds = getSuppliersCountries(suppliers);
        final Countries countries = new Countries(countriesIds);

        final JsonNode schemes = getOrganizationSchemesData(countries);

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

    private JsonNode getOrganizationSchemesData(final Countries countries) {
        LOG.debug("Get data of schemes by country: {}.", countries);
        ResponseEntity<String> response = mdmRestClient.getOrganizationSchemes(countries);
        final String schemesData = response.getBody();
        LOG.debug("Received data of schemes by country: {} - '{}'.", countries, schemesData);
        return jsonUtil.toJsonNode(schemesData).get("data").get("elements");
    }

    private List<String> getSuppliersCountries(final ArrayNode suppliers) {
        return StreamSupport.stream(suppliers.spliterator(), false)
                .map(supplier -> supplier.get("address")
                        .get("addressDetails")
                        .get("country")
                        .get("id").asText())
                .collect(Collectors.toList());
    }

    @Getter
    public static class Countries{
        @JsonProperty("countries")
        private final List<String> countries;

        Countries(final List<String> countries) {
            Objects.requireNonNull(countries);
            this.countries = countries;
        }
    }

}
