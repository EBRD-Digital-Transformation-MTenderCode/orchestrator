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

    public ProcessInstance startProcess(final String processType,
                                        final String operationId,
                                        final Map<String, Object> variables) {
        return runtimeService.startProcessInstanceByKey(processType, operationId, variables);
    }

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

    public void processError(final List<ResponseDetailsDto> details,
                             final String processId,
                             final String operationId) {
        final String errorMessage = Optional.ofNullable(details).map(d -> jsonUtil.toJson(details)).orElse("");
        final String message = "Error in process Id: " + processId + "; message: " + errorMessage;
        messageProducer.sendToPlatform(new PlatformMessage(false, operationId, message));
        terminateProcess(processId, message);
    }

    public void processError(final String error,
                             final String processId,
                             final String operationId) {
        final String message = "Error in process Id: " + processId + "; message: " + error;
        messageProducer.sendToPlatform(new PlatformMessage(false, operationId, message));
        terminateProcess(processId, message);
    }

    public void terminateProcess(final String processId, final String message) {
        LOG.error(message);
        runtimeService.deleteProcessInstance(processId, message);
    }

    public String getText(final String fieldName, final JsonNode responseData, final String processId) {
        try {
            return responseData.get(fieldName).asText();
        } catch (Exception e) {
            terminateProcess(processId, fieldName + " not found.");
        }
        return null;
    }

    public Boolean getBoolean(final String fieldName, final JsonNode responseData, final String processId) {
        try {
            return responseData.get(fieldName).asBoolean();
        } catch (Exception e) {
            terminateProcess(processId, fieldName + " not found.");
        }
        return null;
    }

    public String getTenderPeriodEndDate(final JsonNode jsonData, final String processId) {
        try {
            return jsonData.get("tender").get("tenderPeriod").get("endDate").asText();
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode addTenderPeriodStartDate(final JsonNode jsonData, final String startDate, final String processId) {
        try {
            ((ObjectNode) jsonData.get("tender").get("tenderPeriod")).put("startDate", startDate);
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode addTenderTenderPeriod(final JsonNode jsonData, final JsonNode periodData, final String processId) {
        try {
            ((ObjectNode) jsonData.get("tender")).putObject("tenderPeriod")
                    .put("startDate", periodData.get("startDate").asText())
                    .put("endDate", periodData.get("endDate").asText());
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode addTenderEnquiryPeriod(final JsonNode jsonData, final JsonNode periodData, final String processId) {
        try {
            ((ObjectNode) jsonData.get("tender")).putObject("enquiryPeriod")
                    .put("startDate", periodData.get("startDate").asText())
                    .put("endDate", periodData.get("endDate").asText());
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode addTenderStatus(final JsonNode jsonData, final JsonNode statusData, final String processId) {
        try {
            ((ObjectNode) jsonData).putObject("tender")
                    .put("status", statusData.get("status").asText())
                    .put("statusDetails", statusData.get("statusDetails").asText());
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode addLots(final JsonNode jsonData, final JsonNode lotsData, final String processId) {
        try {
            ((ObjectNode) jsonData).replace("lots", lotsData.get("lots"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode addAwardData(final JsonNode jsonData, final JsonNode awardData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("awardPeriod", awardData.get("awardPeriod"));
            mainNode.replace("awards", awardData.get("awards"));
            mainNode.replace("unsuccessfulLots", awardData.get("unsuccessfulLots"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getUnsuccessfulLots(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.replace("unsuccessfulLots", jsonData.get("unsuccessfulLots"));
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }


    public JsonNode addUpdateBidsStatusData(JsonNode jsonData, JsonNode bidsData, String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("tenderPeriod", bidsData.get("tenderPeriod"));
            mainNode.replace("tenderers", bidsData.get("tenderers"));
            mainNode.replace("updatedBids", bidsData.get("bids"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Override
    public JsonNode addUpdateLotsStatusData(JsonNode jsonData, JsonNode lotsData, String processId) {
        try {
            ((ObjectNode) jsonData).replace("updatedLots", lotsData.get("lots"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }
}
