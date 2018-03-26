package com.procurement.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.config.kafka.MessageProducer;
import com.procurement.orchestrator.domain.EntityAccess;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.domain.PlatformMessage;
import com.procurement.orchestrator.domain.Rules;
import com.procurement.orchestrator.domain.response.ResponseDetailsDto;
import com.procurement.orchestrator.domain.response.ResponseDto;
import com.procurement.orchestrator.exception.OperationException;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.*;
import org.camunda.bpm.engine.RuntimeService;
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

    public void startProcess(final Params params, final Map<String, Object> variables) {
        variables.put("requestId", params.getRequestId());
        runtimeService.startProcessInstanceByKey(params.getProcessType(),  params.getOperationId(), variables);
    }

    public void startProcessCheckRules(final Params params) {
        final Map<String, Object> variables = new HashMap<>();
        variables.put("requestId", params.getRequestId());
        variables.put("checkRules", 1);
        variables.put("message", "");
        runtimeService.startProcessInstanceByKey("checkRules", variables);
    }

    public void startProcessError(final Params params, final String message) {
        final Map<String, Object> variables = new HashMap<>();
        variables.put("requestId", params.getRequestId());
        variables.put("message", message);
        runtimeService.startProcessInstanceByKey("error", variables);
    }

    public void terminateProcess(final String processId, final String message) {
        LOG.error(message);
        runtimeService.deleteProcessInstance(processId, message);
    }

    @Override
    public void terminateProcessWithMessage(final Params params, final String processId, final String message) {
        LOG.error(message);
        runtimeService.deleteProcessInstance(processId, message);
        startProcessError(params, message);
    }

    public JsonNode processResponse(final ResponseEntity<ResponseDto> responseEntity,
                                    final Params params,
                                    final String processId,
                                    final String taskId) {
        if (responseEntity.getBody().getSuccess()) {
            return jsonUtil.toJsonNode(responseEntity.getBody().getData());
        } else {
            operationService.saveOperationException(processId, taskId, jsonUtil.toJsonNode(responseEntity.getBody()));
            final List<ResponseDetailsDto> details = responseEntity.getBody().getDetails();
            final String message = Objects.nonNull(details) ?
                    "Error in process Id: " + processId + "; message: " + jsonUtil.toJson(details) : "";
            runtimeService.deleteProcessInstance(processId, message);
            startProcessError(params, message);
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

    public JsonNode addTenderTenderPeriod(final JsonNode jsonData, final JsonNode periodData, final String processId) {
        try {
            if (Objects.isNull(jsonData.get("tender")))
                ((ObjectNode) jsonData).putObject("tender");
            if (Objects.isNull(jsonData.get("tender").get("tenderPeriod")))
                ((ObjectNode) jsonData.get("tender")).putObject("tenderPeriod");
            final ObjectNode tenderPeriodNode = (ObjectNode) jsonData.get("tender").get("tenderPeriod");
            tenderPeriodNode.replace("startDate", periodData.get("startDate"));
            tenderPeriodNode.replace("endDate", periodData.get("endDate"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode addTenderEnquiryPeriod(final JsonNode jsonData, final JsonNode periodData, final String processId) {
        try {
            if (Objects.isNull(jsonData.get("tender")))
                ((ObjectNode) jsonData).putObject("tender");
            if (Objects.isNull(jsonData.get("tender").get("enquiryPeriod")))
                ((ObjectNode) jsonData.get("tender")).putObject("enquiryPeriod");
            final ObjectNode enquiryPeriodNode = (ObjectNode) jsonData.get("tender").get("enquiryPeriod");
            enquiryPeriodNode.replace("startDate", periodData.get("startDate"));
            enquiryPeriodNode.replace("endDate", periodData.get("endDate"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Override
    public JsonNode addTenderPeriodStartDate(JsonNode jsonData, String startDate, String processId) {
        try {
            if (Objects.isNull(jsonData.get("tender")))
                ((ObjectNode) jsonData).putObject("tender");
            if (Objects.isNull(jsonData.get("tender").get("tenderPeriod")))
                ((ObjectNode) jsonData.get("tender")).putObject("tenderPeriod");
            final ObjectNode tenderPeriodNode = (ObjectNode) jsonData.get("tender").get("tenderPeriod");
            tenderPeriodNode.put("startDate", startDate);
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

    public JsonNode getDocuments(final JsonNode jsonData, final String processId) {
        try {
            final ArrayNode documentsNode = (ArrayNode) jsonData.findPath("documents");
            if (Objects.isNull(documentsNode)) return null;
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.replace("documents", documentsNode);
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setDocuments(final JsonNode jsonData, final JsonNode documentsData, final String processId) {
        try {
            ((ObjectNode) jsonData).replace("documents", documentsData.get("documents"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getDocumentsOfBids(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            final ArrayNode documentsArray = mainNode.putArray("documents");
            final ArrayNode bidsNode = (ArrayNode) jsonData.get("bids");
            for (final JsonNode bidNode : bidsNode) {
                final ArrayNode documentsNode = (ArrayNode) bidNode.get("documents");
                for (final JsonNode docNode : documentsNode) {
                    documentsArray.add(docNode);
                }
            }
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setDocumentsStartDateOfBids(final JsonNode jsonData, final String startDate, final String
            processId) {
        try {
            final ArrayNode bidsNode = (ArrayNode) jsonData.get("bids");
            for (final JsonNode bidNode : bidsNode) {
                final ArrayNode documentsNode = (ArrayNode) bidNode.get("documents");
                for (final JsonNode node : documentsNode) {
                    ((ObjectNode) node).put("datePublished", startDate);
                }
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

    @Override
    public JsonNode getCheckFs(JsonNode jsonData, String processId) {
        try {
            final ArrayNode budgetBreakdownNode = (ArrayNode) jsonData.findPath("budgetBreakdown");
            if (Objects.isNull(budgetBreakdownNode)) return null;
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.replace("budgetBreakdown", budgetBreakdownNode);
            final JsonNode tenderPeriodNode = jsonData.findPath("tenderPeriod");
            if (Objects.isNull(tenderPeriodNode)) return null;
            mainNode.replace("tenderPeriod", tenderPeriodNode);
            final JsonNode classificationNode = jsonData.findPath("classification");
            if (Objects.isNull(classificationNode)) return null;
            mainNode.replace("classification", classificationNode);
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Override
    public JsonNode setCheckFs(JsonNode jsonData, JsonNode responseData, String processId) {
        try {
            final ObjectNode mainNode = ((ObjectNode) jsonData);
            final ObjectNode budgetNode = (ObjectNode) mainNode.get("planning").get("budget");
            budgetNode.replace("budgetBreakdown", responseData.get("budgetBreakdown"));
            mainNode.replace("ei", responseData.get("ei"));
            mainNode.replace("buyer", responseData.get("buyer"));
            mainNode.replace("funder", responseData.get("funder"));
            mainNode.replace("payer", responseData.get("payer"));
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Override
    public JsonNode setCn(JsonNode jsonData, JsonNode responseData, String processId) {
        try {
            final ObjectNode mainNode = ((ObjectNode) jsonData);
            mainNode.replace("planning", responseData.get("planning"));
            mainNode.replace("tender", responseData.get("tender"));
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }
}
