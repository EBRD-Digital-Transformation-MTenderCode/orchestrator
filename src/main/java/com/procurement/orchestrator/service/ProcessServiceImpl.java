package com.procurement.orchestrator.service;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.config.kafka.MessageProducer;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Notification;
import com.procurement.orchestrator.domain.PlatformError;
import com.procurement.orchestrator.domain.PlatformMessage;
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
    private final MessageProducer messageProducer;

    private final JsonUtil jsonUtil;

    public ProcessServiceImpl(final RuntimeService runtimeService,
                              final OperationService operationService,
                              final MessageProducer messageProducer,
                              final JsonUtil jsonUtil) {
        this.operationService = operationService;
        this.runtimeService = runtimeService;
        this.messageProducer = messageProducer;
        this.jsonUtil = jsonUtil;
    }

    public void startProcess(final Context context, final Map<String, Object> variables) {
        variables.put("requestId", context.getRequestId());
        runtimeService.startProcessInstanceByKey(context.getProcessType(), context.getOperationId(), variables);
    }

    public void terminateProcess(final String processId, final String message) {
        LOG.error(message);
        runtimeService.deleteProcessInstance(processId, message);
    }


    public CommandMessage getCommandMessage(final CommandType commandType, final Context context, final JsonNode data) {
        return new CommandMessage(context.getOperationId(), commandType, context, data, ApiVersion.V_0_0_1);
    }

    public JsonNode processResponse(final ResponseEntity<ResponseDto> responseEntity,
                                    final Context context,
                                    final String processId,
                                    final String taskId,
                                    final JsonNode request) {

        final Set<PlatformError> errors = new HashSet<>();
        if (responseEntity.getBody().getDetails() != null) {
            operationService.saveOperationException(processId, taskId, context, request, jsonUtil.toJsonNode(responseEntity.getBody()));
            final List<ResponseDetailsDto> details = responseEntity.getBody().getDetails();
            for (final ResponseDetailsDto detail : details) {
                errors.add(new PlatformError(detail.getCode(), detail.getMessage()));
            }
            context.setErrors(errors);
            sendErrorToPlatform(context);
            runtimeService.deleteProcessInstance(processId, context.getOperationId());
            return null;
        } else if (responseEntity.getBody().getErrors() != null) {
            operationService.saveOperationException(processId, taskId, context, request, jsonUtil.toJsonNode(responseEntity.getBody()));
            final List<ResponseErrorDto> responseErrors = responseEntity.getBody().getErrors();
            for (final ResponseErrorDto error : responseErrors) {
                errors.add(new PlatformError(error.getCode(), error.getDescription()));
            }
            if (errors.size() > 0) {
                context.setErrors(errors);
                sendErrorToPlatform(context);
                runtimeService.deleteProcessInstance(processId, context.getOperationId());
            }
            return null;
        } else {
            return jsonUtil.toJsonNode(responseEntity.getBody().getData());
        }
    }

    public String getText(final String fieldName, final JsonNode jsonData, final String processId) {
        try {
            final JsonNode jsonNode = jsonData.get(fieldName);
            if (jsonNode != null) {
                return jsonNode.asText();
            } else {
                return null;
            }
        } catch (Exception e) {
            terminateProcess(processId, fieldName + " not found.");
        }
        return null;
    }

    public Boolean getBoolean(final String fieldName, final JsonNode jsonData, final String processId) {
        try {
            final JsonNode jsonNode = jsonData.get(fieldName);
            if (jsonNode != null) {
                return jsonNode.asBoolean();
            } else {
                return null;
            }
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

    public JsonNode addLotsAndAwardCriteria(final JsonNode jsonData, final JsonNode lotsData, final String processId) {
        try {
            ((ObjectNode) jsonData).replace("lots", lotsData.get("lots"));
            ((ObjectNode) jsonData).replace("awardCriteria", lotsData.get("awardCriteria"));
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

    public JsonNode addAwards(final JsonNode jsonData, final JsonNode awardsData, final String processId) {
        try {
            ((ObjectNode) jsonData).replace("awards", awardsData.get("awards"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

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

    public Boolean isBidsEmpty(final JsonNode responseData, final String processId) {
        try {
            final ArrayNode bidsNode = (ArrayNode) responseData.get("bids");
            return (bidsNode.size() == 0);
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

    public JsonNode getDocumentsOfAwards(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            final ArrayNode documentsArray = mainNode.putArray("documents");
            final ArrayNode awardsNode = (ArrayNode) jsonData.get("awards");
            for (final JsonNode awardNode : awardsNode) {
                final JsonNode documentsNode = awardNode.get("documents");
                if (documentsNode != null) {
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
                final JsonNode documentsNode = bidNode.get("documents");
                if (documentsNode != null) {
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

    public JsonNode getEiData(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            final ObjectNode tenderNode = mainNode.putObject("tender");
            tenderNode.replace("classification", jsonData.get("tender").get("classification"));
            mainNode.replace("buyer", jsonData.get("buyer"));
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setEiData(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            final ObjectNode tenderNode = (ObjectNode) jsonData.get("tender");
            tenderNode.replace("classification", responseData.get("tender").get("classification"));
            tenderNode.replace("mainProcurementCategory", responseData.get("tender").get("mainProcurementCategory"));
            mainNode.replace("buyer", responseData.get("buyer"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getFsData(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            final ObjectNode amountNode = mainNode.putObject("planning").putObject("budget").putObject("amount");
            final ObjectNode tenderNode = mainNode.putObject("tender");
            amountNode.replace("currency", jsonData.get("planning").get("budget").get("amount").get("currency"));
            tenderNode.replace("procuringEntity", jsonData.get("tender").get("procuringEntity"));
            mainNode.replace("buyer", jsonData.get("buyer"));
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setFsData(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            final ObjectNode tenderNode = (ObjectNode) jsonData.get("tender");
            final ObjectNode amountNode = (ObjectNode) jsonData.get("planning").get("budget").get("amount");
            mainNode.replace("buyer", responseData.get("buyer"));
            tenderNode.replace("procuringEntity", responseData.get("tender").get("procuringEntity"));
            amountNode.replace("currency", responseData.get("planning").get("budget").get("amount").get("currency"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getTenderData(final Boolean itemsAdd, final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            final JsonNode lotsNode = jsonData.get("tender").get("lots");
            final JsonNode procuringEntityNode = jsonData.get("tender").get("procuringEntity");
            final ObjectNode tenderNode = mainNode.putObject("tender");
            tenderNode.replace("lots", lotsNode);
            tenderNode.replace("procuringEntity", procuringEntityNode);
            if (itemsAdd) {
                final JsonNode classificationNode = jsonData.get("tender").get("classification");
                final JsonNode itemsNode = jsonData.get("tender").get("items");
                tenderNode.replace("classification", classificationNode);
                tenderNode.replace("items", itemsNode);
            }
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setTenderData(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode tenderNode = (ObjectNode) jsonData.get("tender");
            final JsonNode tenderResponseNode = responseData.get("tender");
            tenderNode.replace("items", tenderResponseNode.get("items"));
            tenderNode.replace("lots", tenderResponseNode.get("lots"));
            tenderNode.replace("classification", tenderResponseNode.get("classification"));
            tenderNode.replace("submissionMethodRationale", tenderResponseNode.get("submissionMethodRationale"));
            tenderNode.replace("submissionMethodDetails", tenderResponseNode.get("submissionMethodDetails"));
            tenderNode.replace("procurementMethodDetails", tenderResponseNode.get("procurementMethodDetails"));
            tenderNode.replace("eligibilityCriteria", tenderResponseNode.get("eligibilityCriteria"));
            tenderNode.replace("procuringEntity", tenderResponseNode.get("procuringEntity"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getCheckItems(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            final JsonNode itemsNode = jsonData.get("tender").get("items");
            if (itemsNode != null) {
                mainNode.replace("items", itemsNode);
                return mainNode;
            } else {
                return null;
            }
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setCheckItems(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode tenderNode = (ObjectNode) jsonData.get("tender");
            final JsonNode tenderResponseNode = responseData.get("tender");
            if (tenderResponseNode != null) {
                tenderNode.replace("classification", tenderResponseNode.get("classification"));
            }
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

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

    public JsonNode setCheckFs(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            final ObjectNode tenderNode = (ObjectNode) jsonData.get("tender");
            tenderNode.replace("mainProcurementCategory", responseData.get("tender").get("mainProcurementCategory"));
            tenderNode.replace("classification", responseData.get("tender").get("classification"));
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

    public JsonNode getBidTenderersData(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            final JsonNode tenderersNode = jsonData.get("bid").get("tenderers");
            mainNode.replace("tenderers", tenderersNode);
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setBidTenderersData(final JsonNode jsonData, final JsonNode responseData,
                                        final String processId) {
        try {
            final ObjectNode bidNode = (ObjectNode) jsonData.get("bid");
            final JsonNode tenderersResponseNode = responseData.get("tenderers");
            bidNode.replace("tenderers", tenderersResponseNode);
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Override
    public String getEnquiryId(final JsonNode jsonData, final String processId) {
        try {
            final String enquiryId = jsonData.get("enquiry").get("id").asText();
            return enquiryId;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getEnquiryAuthor(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            final JsonNode enquiryNode = jsonData.get("enquiry").get("author");
            mainNode.replace("author", enquiryNode);
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setEnquiryAuthor(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode enquiryNode = (ObjectNode) jsonData.get("enquiry");
            final JsonNode authorResponseNode = responseData.get("author");
            enquiryNode.replace("author", authorResponseNode);
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getEnquiryRelatedLot(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            final JsonNode relatedLotNode = jsonData.get("enquiry").get("relatedLot");
            if (relatedLotNode != null) {
                mainNode.replace("relatedLot", relatedLotNode);
                return mainNode;
            } else {
                return null;
            }
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public void sendErrorToPlatform(final Context context) {
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
