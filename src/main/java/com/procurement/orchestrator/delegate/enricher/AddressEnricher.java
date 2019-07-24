package com.procurement.orchestrator.delegate.enricher;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.delegate.kafka.MessageProducer;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Notification;
import com.procurement.orchestrator.domain.PlatformError;
import com.procurement.orchestrator.domain.PlatformMessage;
import com.procurement.orchestrator.domain.dto.MDMErrorResponse;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.exception.MDMException;
import com.procurement.orchestrator.rest.MdmRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class AddressEnricher implements JavaDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(AddressEnricher.class);

    private final RuntimeService runtimeService;
    private final MessageProducer messageProducer;
    private final MdmRestClient mdmRestClient;
    private final OperationService operationService;
    private final JsonUtil jsonUtil;

    public AddressEnricher(
        final RuntimeService runtimeService,
        final MessageProducer messageProducer,
        final MdmRestClient mdmRestClient,
        final OperationService operationService,
        final JsonUtil jsonUtil
    ) {
        this.runtimeService = runtimeService;
        this.messageProducer = messageProducer;
        this.mdmRestClient = mdmRestClient;
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityId());

        final OperationStepEntity operationStepEntity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, operationStepEntity.getContext());
        final String lang = context.getLanguage().toUpperCase();

        try {
            final JsonNode updatedData = updateData(operationStepEntity, lang);
            final JsonNode prevData = jsonUtil.toJsonNode(operationStepEntity.getResponseData());
            operationService.saveOperationStep(execution, operationStepEntity, prevData, updatedData);
        } catch (MDMException exception) {
            final Set<PlatformError> errors = new HashSet<>();
            final MDMErrorResponse errorResponse = jsonUtil.toObject(MDMErrorResponse.class, exception.getResponseBody());
            for (final MDMErrorResponse.Error error : errorResponse.getErrors()) {
                errors.add(new PlatformError(error.getCode(), error.getDescription()));
            }
            context.setErrors(errors);
            final String processId = execution.getProcessInstanceId();
            runtimeService.deleteProcessInstance(processId, context.getOperationId());
            sendErrorToPlatform(context);
        }
    }

    private JsonNode updateData(final OperationStepEntity operationStepEntity, final String lang) {
        final JsonNode updatedData = jsonUtil.toJsonNode(operationStepEntity.getResponseData());
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
        return updatedData;
    }

    private JsonNode getCountryData(final String countryId, final String lang) {
        LOG.debug("Get data of a country by id: {}, and lang: {}.", countryId, lang);
        ResponseEntity<String> response = mdmRestClient.getCountry(countryId, lang);
        checkResponseFromMDM(response);
        final String countryData = response.getBody();
        LOG.debug("Received data of a country by id: {}, and lang: {} - '{}'.", countryId, lang, countryData);
        return jsonUtil.toJsonNode(countryData).get("data");
    }

    private JsonNode getRegionData(final String countryId, final String regionId, final String lang) {
        LOG.debug("Get data of a region by id: {} and country id: {} and lang: {}.", regionId, countryId, lang);
        ResponseEntity<String> response = mdmRestClient.getRegion(countryId, regionId, lang);
        checkResponseFromMDM(response);
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
    ) {
        LOG.debug(
            "Get data of a locality by id: {} and country id: {} and region id: {} and lang: {}.",
            localityId, countryId, regionId, lang
        );
        ResponseEntity<String> response = mdmRestClient.getLocality(countryId, regionId, localityId, lang);
        checkResponseFromMDM(response);
        final String localityData = response.getBody();
        LOG.debug(
            "Received data of a locality by id: {} and country id: {} and region id: {} and lang: {} - '{}'.",
            localityId, countryId, regionId, lang, localityData
        );
        return jsonUtil.toJsonNode(localityData).get("data");
    }

    private void checkResponseFromMDM(ResponseEntity<String> response) {
        if (response.getStatusCode() != HttpStatus.OK)
            throw new MDMException(response.getBody());
    }

    private void sendErrorToPlatform(final Context context) {
        final PlatformMessage message = new PlatformMessage();
        message.setOperationId(context.getOperationId());
        message.setResponseId(UUIDs.timeBased().toString());
        message.setErrors(context.getErrors());

        final Notification notification = new Notification(
            UUID.fromString(context.getOwner()),
            UUID.fromString(context.getOperationId()),
            jsonUtil.toJson(message)
        );
        messageProducer.sendToPlatform(notification);
    }
}
