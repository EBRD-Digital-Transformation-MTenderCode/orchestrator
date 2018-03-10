package com.procurement.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.EntityAccess;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.domain.PlatformMessage;
import com.procurement.orchestrator.domain.response.ResponseDetailsDto;
import com.procurement.orchestrator.domain.response.ResponseDto;
import com.procurement.orchestrator.kafka.MessageProducer;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.*;
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

    public void terminateProcess(final String processId, final String message) {
        LOG.error(message);
        runtimeService.deleteProcessInstance(processId, message);
    }

    public JsonNode processResponse(final ResponseEntity<ResponseDto> responseEntity,
                                    final String processId,
                                    final String operationId,
                                    final String taskId) {
        if (responseEntity.getBody().getSuccess()) {
            return jsonUtil.toJsonNode(responseEntity.getBody().getData());
        } else {
            operationService.saveOperationException(processId, taskId, jsonUtil.toJsonNode(responseEntity.getBody()));
            final List<ResponseDetailsDto> details = responseEntity.getBody().getDetails();
            final String message = Objects.nonNull(details) ?
                    "Error in process Id: " + processId + "; message: " + jsonUtil.toJson(details) : "";
            messageProducer.sendToPlatform(new PlatformMessage(false, operationId, message));
            runtimeService.deleteProcessInstance(processId, message);
            return null;
        }
    }

    public String getText(final String fieldName, final JsonNode jsonData, final String processId) {
        try {
            return jsonData.get(fieldName).asText();
        } catch (Exception e) {
            terminateProcess(processId, fieldName + " not found.");
        }
        return null;
    }

    public Boolean getBoolean(final String fieldName, final JsonNode jsonData, final String processId) {
        try {
            return jsonData.get(fieldName).asBoolean();
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

    public Params addAccessToParams(final Params params,
                                    final String entityType,
                                    final String entityId,
                                    final JsonNode responseData,
                                    final String processId) {
        final String entityToken = getText("token", responseData, processId);
        params.setAccess(Arrays.asList(new EntityAccess(entityType, entityId, entityToken)));
        return params;
    }

    @Override
    public Params addBidAccessToParams(Params params, JsonNode responseData, String processId) {
        final String entityToken = getText("token", responseData, processId);
        final String entityId = getText("bidId", responseData, processId);
        params.setAccess(Arrays.asList(new EntityAccess("bid", entityId, entityToken)));
        return params;
    }

    public Params addAwardAccessToParams(Params params, JsonNode responseData, String processId) {
        final ArrayNode awardsNode = (ArrayNode) responseData.get("awards");
        final List<EntityAccess> accesses = new ArrayList<>();
        for (final JsonNode awardNode : awardsNode) {
            accesses.add(new EntityAccess(
                    "award",
                    awardNode.get("id").asText(),
                    awardNode.get("token").asText())
            );
        }
        params.setAccess(accesses);
        return params;
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
            mainNode.replace("bids", bidsData.get("bids"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode addBids(JsonNode jsonData, JsonNode lotsData, String processId) {
        try {
            ((ObjectNode) jsonData).replace("bids", lotsData.get("bids"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public String getAwardRelatedBid(final JsonNode jsonData, final String processId) {
        try {
            return jsonData.get("award").get("relatedBid").asText();
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public String getAwardStatusDetails(final JsonNode jsonData, final String processId) {
        try {
            return jsonData.get("award").get("statusDetails").asText();
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode addUpdatedBid(final JsonNode jsonData, final JsonNode bidData, final String processId) {
        try {
            ((ObjectNode) jsonData).replace("bid", bidData.get("bid"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getDocuments(final JsonNode jsonData, final String processId, final String operationId) {
        try {
            final JsonNode tenderNode = jsonData.get("tender");
            final ArrayNode documentsNode = (ArrayNode) tenderNode.get("documents");
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            final ArrayNode documentsArray = mainNode.putArray("documents");
            for (final JsonNode docNode : documentsNode) {
                ObjectNode idNode = jsonUtil.createObjectNode();
                idNode.put("id", docNode.get("id").asText());
                documentsArray.add(idNode);
            }
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setDatePublished(final JsonNode jsonData, final String startDate, final String processId, final String operationId) {
        try {
            final JsonNode tenderNode = jsonData.get("tender");
            final ArrayNode documentsNode = (ArrayNode) tenderNode.get("documents");
            for (final JsonNode fileNode : documentsNode) {
                ((ObjectNode) fileNode).put("datePublished", startDate);
            }
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode addStandstillPeriod(final JsonNode jsonData, final String startDate, final String endDate, final String processId) {
        try {
            ((ObjectNode) jsonData).putObject("standstillPeriod")
                    .put("startDate", startDate)
                    .put("endDate", endDate);
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }
}
