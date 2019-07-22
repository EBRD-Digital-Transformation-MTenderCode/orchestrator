package com.procurement.orchestrator.delegate.enricher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
public class AddressEnricher implements JavaDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(AddressEnricher.class);

    private final MdmRestClient mdmRestClient;
    private final OperationService operationService;
    private final JsonUtil jsonUtil;

    public AddressEnricher(
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
        final String lang = context.getLanguage().toUpperCase();

        final JsonNode updatedData = jsonUtil.toJsonNode(entity.getResponseData());
        final ArrayNode suppliers = (ArrayNode) (updatedData.get("award").get("suppliers"));
        for (final JsonNode supplier : suppliers) {
            final ObjectNode address = (ObjectNode) (supplier.get("address").get("addressDetails"));

            final String countryId = address.get("country").get("id").asText();
            final JsonNode countryData = getCountryData(countryId, lang);
            address.set("country", countryData);

            final String regionId = address.get("region").get("id").asText();
            final JsonNode regionData = getRegionData(countryId, regionId, lang);
            address.set("region", regionData);

            final String localityId = address.get("locality").get("id").asText();
            final JsonNode localityData = getLocalityData(countryId, regionId, localityId, lang);
            address.set("locality", localityData);
        }

        final JsonNode prevData = jsonUtil.toJsonNode(entity.getResponseData());
        operationService.saveOperationStep(execution, entity, prevData, updatedData);
    }

    private JsonNode getCountryData(final String countryId, final String lang) throws Exception {
        LOG.debug("Get data of a country by id: {}, and lang: {}.", countryId, lang);
        ResponseEntity<String> response = mdmRestClient.getCountry(countryId, lang);
        final String countryData = response.getBody();
        LOG.debug("Received data of a country by id: {}, and lang: {} - '{}'.", countryId, lang, countryData);
        return jsonUtil.toJsonNode(countryData).get("data");
    }

    private JsonNode getRegionData(final String countryId, final String regionId, final String lang) throws Exception {
        LOG.debug("Get data of a region by id: {} and country id: {} and lang: {}.", regionId, countryId, lang);
        ResponseEntity<String> response = mdmRestClient.getRegion(countryId, regionId, lang);
        final String regionData = response.getBody();
        LOG.debug(
            "Received data of a region by id: {} and country id: {} and lang: {} - '{}'.",
            regionId, countryId, lang, regionData
        );
        return jsonUtil.toJsonNode(regionData).get("data");
    }

    private JsonNode getLocalityData(
        final String countryId,
        final String regionId,
        final String localityId,
        final String lang
    ) throws Exception {
        LOG.debug(
            "Get data of a locality by id: {} and country id: {} and region id: {} and lang: {}.",
            localityId, countryId, regionId, lang
        );
        ResponseEntity<String> response = mdmRestClient.getLocality(countryId, regionId, localityId, lang);
        final String localityData = response.getBody();
        LOG.debug(
            "Received data of a locality by id: {} and country id: {} and region id: {} and lang: {} - '{}'.",
            localityId, countryId, regionId, lang, localityData
        );
        return jsonUtil.toJsonNode(localityData).get("data");
    }
}
