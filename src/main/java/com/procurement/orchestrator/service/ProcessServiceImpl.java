package com.procurement.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.EntityAccess;
import com.procurement.orchestrator.domain.dto.*;
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

    private final OperationService operationService;

    private final JsonUtil jsonUtil;

    public ProcessServiceImpl(final RuntimeService runtimeService,
                              final OperationService operationService,
                              final JsonUtil jsonUtil) {
        this.operationService = operationService;
        this.runtimeService = runtimeService;
        this.jsonUtil = jsonUtil;
    }

    public void startProcess(final Context context, final Map<String, Object> variables) {
        variables.put("requestId", context.getRequestId());
        runtimeService.startProcessInstanceByKey(context.getProcessType(), context.getOperationId(), variables);
    }

    public void startProcessCheckRules(final Context context) {
        final Map<String, Object> variables = new HashMap<>();
        variables.put("requestId", context.getRequestId());
        variables.put("checkRules", 1);
        variables.put("message", "");
        runtimeService.startProcessInstanceByKey("checkRules", variables);
    }

    public void startProcessError(final Context context, final String message) {
        final Map<String, Object> variables = new HashMap<>();
        variables.put("requestId", context.getRequestId());
        variables.put("message", message);
        runtimeService.startProcessInstanceByKey("error", variables);
    }

    public void terminateProcess(final String processId, final String message) {
        LOG.error(message);
        runtimeService.deleteProcessInstance(processId, message);
    }


    public void terminateProcessWithMessage(final Context context, final String processId, final String message) {
        LOG.error(message);
        runtimeService.deleteProcessInstance(processId, message);
        startProcessError(context, message);
    }

    public CommandMessage getCommandMessage(final CommandType commandType, final Context context, final JsonNode data) {
        return new CommandMessage(context.getOperationId(), commandType, context, data, ApiVersion.V_0_0_1);
    }

    public JsonNode processResponse(final ResponseEntity<ResponseDto> responseEntity,
                                    final Context context,
                                    final String processId,
                                    final String taskId,
                                    final JsonNode request) {
        if (responseEntity.getBody().getDetails() != null) {
            operationService.saveOperationException(processId, taskId, context, request, jsonUtil.toJsonNode(responseEntity.getBody()));
            final List<ResponseDetailsDto> details = responseEntity.getBody().getDetails();
            final String message = Objects.nonNull(details) ?
                    "Error in operation: " + context.getOperationId() + "; message: " + jsonUtil.toJson(details) : "";
            runtimeService.deleteProcessInstance(processId, message);
            startProcessError(context, message);
            return null;
        } else if (responseEntity.getBody().getErrors() != null) {
            operationService.saveOperationException(processId, taskId, context, request, jsonUtil.toJsonNode(responseEntity.getBody()));
            final List<ResponseErrorDto> errors = responseEntity.getBody().getErrors();
            final String message = Objects.nonNull(errors) ?
                    "Error in operation: " + context.getOperationId() + "; message: " + jsonUtil.toJson(errors) : "";
            runtimeService.deleteProcessInstance(processId, message);
            startProcessError(context, message);
            return null;
        } else {
            return jsonUtil.toJsonNode(responseEntity.getBody().getData());
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
            if (Objects.nonNull(processId)) terminateProcess(processId, e.getMessage());
            return null;
        }
    }


    public Context addAccessToContext(final Context context,
                                      final String entityType,
                                      final String entityId,
                                      final JsonNode responseData,
                                      final String processId) {
        final String entityToken = getText("token", responseData, processId);
        context.setAccess(Arrays.asList(new EntityAccess(entityType, entityId, entityToken)));
        return context;
    }

    @Override
    public Context addBidAccessToContext(final Context context, final JsonNode responseData, final String processId) {
        final String entityToken = getText("token", responseData, processId);
        final String entityId = getText("bidId", responseData, processId);
        context.setAccess(Arrays.asList(new EntityAccess("bid", entityId, entityToken)));
        return context;
    }

    public Context addAwardAccessToContext(final Context context, final JsonNode responseData, final String processId) {
        final ArrayNode awardsNode = (ArrayNode) responseData.get("awards");
        final List<EntityAccess> accesses = new ArrayList<>();
        for (final JsonNode awardNode : awardsNode) {
            if (awardNode.get("token") != null) {
                accesses.add(new EntityAccess(
                        "award",
                        awardNode.get("id").asText(),
                        awardNode.get("token").asText())
                );
            }
        }
        context.setAccess(accesses);
        return context;
    }

    public Context addContractAccessToContext(final Context context, final JsonNode responseData,
                                              final String processId) {
        final ArrayNode contractsNode = (ArrayNode) responseData.get("contracts");
        final List<EntityAccess> accesses = new ArrayList<>();
        for (final JsonNode awardNode : contractsNode) {
            accesses.add(new EntityAccess(
                    "contract",
                    awardNode.get("id").asText(),
                    awardNode.get("token").asText())
            );
        }
        context.setAccess(accesses);
        return context;
    }

    public JsonNode addTenderTenderPeriod(final JsonNode jsonData, final JsonNode periodData,
                                          final String processId) {
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

    @Override
    public JsonNode addTenderTenderPeriodStartDate(final JsonNode jsonData, final String startDate, final String
            processId) {
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

    public JsonNode addTenderEnquiryPeriod(final JsonNode jsonData, final JsonNode periodData,
                                           final String processId) {
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

    public JsonNode addLotsAndItems(final JsonNode jsonData, final JsonNode data, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("lots", data.get("lots"));
            mainNode.replace("items", data.get("items"));
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

    @Override
    public JsonNode addAwards(final JsonNode jsonData, final JsonNode awardsData, final String processId) {
        try {
            ((ObjectNode) jsonData).replace("awards", awardsData.get("awards"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Override
    public JsonNode addCans(final JsonNode jsonData, final JsonNode cansData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("cans", cansData.get("cans"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Override
    public JsonNode addContracts(final JsonNode jsonData, final JsonNode data, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("cans", data.get("cans"));
            mainNode.replace("contracts", data.get("contracts"));
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

    @Override
    public JsonNode getTenderLots(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.replace("lots", jsonData.get("tender").get("lots"));
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode addUpdateBidsStatusData(final JsonNode jsonData, final JsonNode bidsData,
                                            final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("tenderPeriod", bidsData.get("tenderPeriod"));
            mainNode.replace("bids", bidsData.get("bids"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode addBidsAndTenderPeriod(final JsonNode jsonData, final JsonNode responseData,
                                           final String processId) {
        try {
            ((ObjectNode) jsonData).replace("bids", responseData.get("bids"));
            addTenderTenderPeriod(jsonData, responseData.get("tenderPeriod"), processId);
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode addBids(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            ((ObjectNode) jsonData).replace("bids", responseData.get("bids"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Override
    public Boolean isBidsEmpty(final JsonNode responseData, final String processId) {
        try {
            final ArrayNode bidsNode = (ArrayNode) responseData.get("bids");
            return bidsNode.size() == 0;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Override
    public Boolean isAwardsEmpty(final JsonNode responseData, final String processId) {
        try {
            final ArrayNode awardsNode = (ArrayNode) responseData.get("awards");
            return awardsNode.size() == 0;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Override
    public String getFsId(final JsonNode jsonData, final String processId) {
        try {
            return jsonData.get("fs").get("ocid").asText();
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Override
    public String getFsToken(final JsonNode jsonData, final String processId) {
        try {
            return jsonData.get("fs").get("token").asText();
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


    public String getLotId(final JsonNode jsonData, final String processId) {
        try {
            final ArrayNode lotsNode = (ArrayNode) jsonData.get("award").get("relatedLots");
            return lotsNode.get(0).asText();
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

    public JsonNode getNextAward(final JsonNode jsonData, final String processId) {
        try {
            return jsonData.get("nextAward");
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

    @Override
    public JsonNode addUpdatedLot(final JsonNode jsonData, final JsonNode lotData, final String processId) {
        try {
            ((ObjectNode) jsonData).replace("lot", lotData.get("lot"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getDocumentsOfTender(final JsonNode jsonData, final String processId) {
        try {
            final JsonNode tenderNode = jsonData.get("tender");
            final JsonNode documentsNode = tenderNode.findPath("documents");
            if (documentsNode.isMissingNode()) return null;
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.replace("documents", documentsNode);
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setDocumentsOfTender(final JsonNode jsonData, final JsonNode documentsData,
                                         final String processId) {
        try {
            final ObjectNode tenderNode = (ObjectNode) jsonData.get("tender");
            tenderNode.replace("documents", documentsData.get("documents"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getDocumentsOfAward(final JsonNode jsonData, final String processId) {
        try {
            final JsonNode awardNode = jsonData.get("award");
            final JsonNode documentsNode = awardNode.findPath("documents");
            if (documentsNode.isMissingNode()) return null;
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.replace("documents", documentsNode);
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setDocumentsOfAward(final JsonNode jsonData, final JsonNode documentsData,
                                        final String processId) {
        try {
            final ObjectNode awardNode = (ObjectNode) jsonData.get("award");
            awardNode.replace("documents", documentsData.get("documents"));
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
                final JsonNode documentsNode = bidNode.findPath("documents");
                if (!documentsNode.isMissingNode()) {
                    for (final JsonNode docNode : documentsNode) {
                        documentsArray.add(docNode);
                    }
                }
            }
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setDocumentsOfBids(final JsonNode jsonData, final JsonNode documentsData,
                                       final String processId) {
        try {
            ((ObjectNode) jsonData).replace("documents", documentsData.get("documents"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode addStandstillPeriod(final JsonNode jsonData, final String startDate, final String endDate,
                                        final String processId) {
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
    public JsonNode setAccessData(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("planning", responseData.get("planning"));
            mainNode.replace("tender", responseData.get("tender"));
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Override
    public JsonNode getAccessData(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.replace("planning", jsonData.get("planning"));
            mainNode.replace("tender", jsonData.get("tender"));
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Override
    public JsonNode setTender(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("tender", responseData.get("tender"));
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Override
    public JsonNode getClassificationOfTender(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.replace("classification", jsonData.get("tender").get("classification"));
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Override
    public JsonNode setClassificationOfTender(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode tenderNode = (ObjectNode) jsonData.get("tender");
            tenderNode.replace("classification", responseData.get("classification"));
            tenderNode.replace("mainProcurementCategory", responseData.get("mainProcurementCategory"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Override
    public JsonNode getCurrencyOfBudget(final JsonNode jsonData, final String processId) {
        try {
            final JsonNode currencyNode = jsonData.get("planning").get("budget").get("amount").get("currency");
            if (currencyNode == null) return null;
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.replace("currency", currencyNode);
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Override
    public JsonNode getTenderInfo(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            final JsonNode itemsNode = jsonData.get("tender").get("items");
            final JsonNode classificationNode = jsonData.get("tender").get("classification");
            final ObjectNode tenderNode = mainNode.putObject("tender");
            tenderNode.replace("items", itemsNode);
            tenderNode.replace("classification", classificationNode);
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Override
    public JsonNode setTenderInfo(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode tenderNode = (ObjectNode) jsonData.get("tender");
            final JsonNode tenderResponseNode = jsonData.get("tender");
            tenderNode.replace("items", tenderResponseNode.get("items"));
            tenderNode.replace("classification", tenderResponseNode.get("classification"));
            tenderNode.replace("submissionMethodRationale", tenderResponseNode.get("submissionMethodRationale"));
            tenderNode.replace("submissionMethodDetails", tenderResponseNode.get("submissionMethodDetails"));
            tenderNode.replace("procurementMethodDetails", tenderResponseNode.get("procurementMethodDetails"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Override
    public JsonNode getCheckFs(final JsonNode jsonData, final String startDate, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.replace("planning", jsonData.get("planning"));
            final ObjectNode tenderNode = mainNode.putObject("tender");
            tenderNode.replace("classification", jsonData.get("tender").get("classification"));
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Override
    public JsonNode setCheckFs(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            final ObjectNode tenderNode = (ObjectNode) jsonData.get("tender");
            tenderNode.replace("mainProcurementCategory", responseData.get("tender").get("mainProcurementCategory"));
            mainNode.replace("planning", responseData.get("planning"));
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
}
