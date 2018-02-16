package com.procurement.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.dto.ResponseDetailsDto;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import com.procurement.orchestrator.kafka.MessageProducer;
import com.procurement.orchestrator.kafka.dto.PlatformMessage;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ProcessServiceImpl implements ProcessService {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessServiceImpl.class);

    private final RuntimeService runtimeService;

    private final MessageProducer messageProducer;

    private final OperationService operationService;

    private final JsonUtil jsonUtil;

    public ProcessServiceImpl(final RuntimeService runtimeService,
                              final MessageProducer messageProducer,
                              final OperationService operationService,
                              final JsonUtil jsonUtil) {
        this.operationService = operationService;
        this.runtimeService = runtimeService;
        this.messageProducer = messageProducer;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public ProcessInstance startProcess(final String processType, final String operationId) {
        return runtimeService.startProcessInstanceByKey(processType, operationId);
    }

    @Override
    public ProcessInstance startProcess(String processType, String operationId, Map<String, Object> variables) {
        return runtimeService.startProcessInstanceByKey(processType, operationId, variables);
    }

    @Override
    public JsonNode processResponse(final ResponseEntity<ResponseDto> responseEntity,
                                    final String processId,
                                    final String operationId,
                                    final String taskId) {
        if (responseEntity.getBody().getSuccess()) {
            return jsonUtil.toJsonNode(responseEntity.getBody().getData());
        } else {
            operationService.saveOperationException(processId, taskId, jsonUtil.toJsonNode(responseEntity.getBody()));
            processError(responseEntity.getBody().getDetails(), processId, operationId);
            return null;
        }
    }

    @Override
    public void processException(final String error,
                                 final String processId) {
        try {
            LOG.info("Exception in process Id: " + processId + "; message: " + error);
            runtimeService.suspendProcessInstanceById(processId);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void processError(List<ResponseDetailsDto> details, String processId, String operationId) {
        final String errorMessage = Optional.ofNullable(details).map(d -> jsonUtil.toJson(details)).orElse("");
        final String message = "Error in process Id: " + processId + "; message: " + errorMessage;
        LOG.info(message);
        messageProducer.sendToPlatform(new PlatformMessage(operationId, message));
        terminateProcess(processId, message);
    }

    @Override
    public void processError(String error, String processId, String operationId) {
        final String message = "Error in process Id: " + processId + "; message: " + error;
        LOG.info(message);
        messageProducer.sendToPlatform(new PlatformMessage(operationId, message));
        terminateProcess(processId, message);
    }

    @Override
    public void terminateProcess(String processId, String message) {
        LOG.error(message);
        runtimeService.deleteProcessInstance(processId, message);
    }

    @Override
    public String getText(String fieldName, JsonNode responseData, String processId) {
        try {
            return responseData.get(fieldName).asText();
        } catch (Exception e) {
            terminateProcess(processId, fieldName + " not found.");
        }
        return null;
    }

    @Override
    public Boolean getBoolean(String fieldName, JsonNode responseData, String processId) {
        try {
            return responseData.get(fieldName).asBoolean();
        } catch (Exception e) {
            terminateProcess(processId, fieldName + " not found.");
        }
        return null;
    }

    public String getTenderPeriodEndDate(final JsonNode jsonData, final String processId) {
        try {
            final JsonNode tenderNode = jsonData.get("tender");
            final JsonNode tenderPeriodNode = tenderNode.get("tenderPeriod");
            return tenderPeriodNode.get("endDate").asText();
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Override
    public JsonNode addTenderPeriodStartDate(final JsonNode jsonData, final String startDate, final String processId) {
        try {
            final JsonNode tenderNode = jsonData.get("tender");
            final JsonNode tenderPeriodNode = tenderNode.get("tenderPeriod");
            ((ObjectNode) tenderPeriodNode).put("startDate", startDate);
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Override
    public JsonNode addTenderTenderPeriod(final JsonNode jsonData, final JsonNode periodData, final String processId) {
        try {
            final JsonNode tenderNode = jsonData.get("tender");
            ObjectNode periodNode = ((ObjectNode) tenderNode).putObject("tenderPeriod");
            periodNode
                    .put("startDate", periodData.get("startDate").asText())
                    .put("endDate", periodData.get("endDate").asText());
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Override
    public JsonNode addTenderEnquiryPeriod(final JsonNode jsonData, final JsonNode periodData, final String processId) {
        try {
            final JsonNode tenderNode = jsonData.get("tender");
            ObjectNode enquiryPeriodNode = ((ObjectNode) tenderNode).putObject("enquiryPeriod");
            enquiryPeriodNode
                    .put("startDate", periodData.get("startDate").asText())
                    .put("endDate", periodData.get("endDate").asText());
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Override
    public JsonNode addTenderStatus(final JsonNode jsonData, final JsonNode statusData, final String processId) {
        try {
            ObjectNode statusNode = ((ObjectNode) jsonData).putObject("tender");
            statusNode
                    .put("status", statusData.get("status").asText())
                    .put("statusDetails", statusData.get("statusDetails").asText());
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Override
    public JsonNode addLots(JsonNode jsonData, JsonNode lotsData, String processId) {
        try {
            ((ObjectNode) jsonData).put("lots", jsonUtil.toJson(lotsData.get("lots").asText()));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Override
    public JsonNode addAwardData(JsonNode jsonData, JsonNode awardData, String processId) {
        try {
            ObjectNode objectNode = (ObjectNode) jsonData;
            objectNode.put("awardPeriod", jsonUtil.toJson(awardData.get("awardPeriod").asText()));
            objectNode.put("awards", jsonUtil.toJson(awardData.get("awards").asText()));
            objectNode.put("unsuccessfulLots", jsonUtil.toJson(awardData.get("unsuccessfulLots").asText()));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public String getUnsuccessfulLots(final JsonNode jsonData, final String processId) {
        try {
            final JsonNode tenderNode = jsonData.get("unsuccessfulLots");
            final JsonNode tenderPeriodNode = tenderNode.get("tenderPeriod");
            return tenderPeriodNode.get("endDate").asText();
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }
}
