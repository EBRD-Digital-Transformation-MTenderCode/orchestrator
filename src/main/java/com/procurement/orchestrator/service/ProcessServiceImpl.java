package com.procurement.orchestrator.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.procurement.orchestrator.delegate.kafka.MessageProducer;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Notification;
import com.procurement.orchestrator.domain.PlatformError;
import com.procurement.orchestrator.domain.PlatformMessage;
import com.procurement.orchestrator.domain.dto.command.CommandMessage;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.dto.command.ResponseErrorDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.utils.JsonUtil;
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
        final String description;
        if (Objects.nonNull(message)) {
            description = message;
        } else {
            description = "Data processing exception.";
        }
        LOG.error(message);
        final Set<PlatformError> errors = new HashSet<>();
        errors.add(new PlatformError("400.00.00.00", description));
        final OperationStepEntity entity = operationService.getOperationStep(processId, "SaveFirstOperationTask");
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        context.setErrors(errors);
        runtimeService.deleteProcessInstance(processId, context.getOperationId());
        sendErrorToPlatform(context);
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

    public JsonNode getCommandMessage(final Enum command, final Context context, final JsonNode data) {
        final CommandMessage commandMessage = new CommandMessage(
            context.getRequestId(),
            command,
            context,
            data,
            CommandMessage.ApiVersion.V_0_0_1);
        return jsonUtil.toJsonNode(commandMessage);
    }

    public JsonNode processResponse(final ResponseEntity<ResponseDto> responseEntity,
                                    final Context context,
                                    final String processId,
                                    final String taskId,
                                    final JsonNode request) {

        final Set<PlatformError> errors = new HashSet<>();
        if (responseEntity.getBody().getErrors() != null) {
            operationService.saveOperationException(processId, taskId, context, request, jsonUtil.toJsonNode(responseEntity.getBody()));
            final List<ResponseErrorDto> responseErrors = responseEntity.getBody().getErrors();
            for (final ResponseErrorDto error : responseErrors) {
                errors.add(new PlatformError(error.getCode(), error.getDescription()));
            }
            if (errors.size() > 0) {
                context.setErrors(errors);
                runtimeService.deleteProcessInstance(processId, context.getOperationId());
                sendErrorToPlatform(context);
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

    public String getString(final String fieldName, final JsonNode jsonData, final String processId) {
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

    public void setPreQualificationPeriodStartDate(JsonNode jsonData, String startDate, String processId) {
        try {
            final ObjectNode enquiryPeriodNode = (ObjectNode) jsonData.get("preQualification").get("period");
            enquiryPeriodNode.put("startDate", startDate);
        } catch (Exception e) {
            if (Objects.nonNull(processId)) terminateProcess(processId, e.getMessage());
        }
    }

    public void setEnquiryPeriodStartDate(JsonNode jsonData, String startDate, String processId) {
        try {
            final ObjectNode enquiryPeriodNode = (ObjectNode) jsonData.get("tender").get("enquiryPeriod");
            enquiryPeriodNode.put("startDate", startDate);
        } catch (Exception e) {
            if (Objects.nonNull(processId)) terminateProcess(processId, e.getMessage());
        }
    }

    public void setTenderPeriodStartDate(JsonNode jsonData, String startDate, String processId) {
        try {
            final ObjectNode tenderPeriodNode = (ObjectNode) jsonData.get("tender").get("tenderPeriod");
            tenderPeriodNode.put("startDate", startDate);
        } catch (Exception e) {
            if (Objects.nonNull(processId)) terminateProcess(processId, e.getMessage());
        }
    }

    public JsonNode setCheckEnquiryPeriod(JsonNode jsonData, JsonNode respData, String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("setExtendedPeriod", respData.get("setExtendedPeriod"));
            mainNode.replace("isEnquiryPeriodChanged", respData.get("isEnquiryPeriodChanged"));
            if (Objects.isNull(jsonData.get("tender")))
                ((ObjectNode) jsonData).putObject("tender");
            if (Objects.isNull(jsonData.get("tender").get("enquiryPeriod")))
                ((ObjectNode) jsonData.get("tender")).putObject("enquiryPeriod");
            final ObjectNode enquiryPeriodNode = (ObjectNode) jsonData.get("tender").get("enquiryPeriod");
            enquiryPeriodNode.replace("startDate", respData.get("startDate"));
            enquiryPeriodNode.replace("endDate", respData.get("endDate"));
            setTenderPeriodStartDate(jsonData, respData.get("endDate").asText(), processId);
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getCheckTenderPeriod(JsonNode jsonData, String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.replace("setExtendedPeriod", jsonData.get("setExtendedPeriod"));
            mainNode.replace("isEnquiryPeriodChanged", jsonData.get("isEnquiryPeriodChanged"));
            mainNode.replace("enquiryPeriod", jsonData.get("tender").get("enquiryPeriod"));
            mainNode.replace("tenderPeriod", jsonData.get("tender").get("tenderPeriod"));
            return mainNode;
        } catch (Exception e) {
            if (Objects.nonNull(processId)) terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setCheckTenderPeriod(JsonNode jsonData, JsonNode respData, String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("isTenderPeriodChanged", respData.get("isTenderPeriodChanged"));
            if (Objects.isNull(jsonData.get("tender")))
                ((ObjectNode) jsonData).putObject("tender");
            if (Objects.isNull(jsonData.get("tender").get("tenderPeriod")))
                ((ObjectNode) jsonData.get("tender")).putObject("tenderPeriod");
            final ObjectNode enquiryPeriodNode = (ObjectNode) jsonData.get("tender").get("tenderPeriod");
            enquiryPeriodNode.replace("startDate", respData.get("startDate"));
            enquiryPeriodNode.replace("endDate", respData.get("endDate"));
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public String getTenderPeriodEndDate(final JsonNode jsonData, final String processId) {
        try {
            return jsonData.get("tender").get("tenderPeriod").get("endDate").asText();
        } catch (Exception e) {
            if (Objects.nonNull(processId)) terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public String getEnquiryPeriodEndDate(JsonNode jsonData, String processId) {
        try {
            return jsonData.get("tender").get("enquiryPeriod").get("endDate").asText();
        } catch (Exception e) {
            if (Objects.nonNull(processId)) terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getTenderPeriod(JsonNode jsonData, String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.replace("tenderPeriod", jsonData.get("tender").get("tenderPeriod"));
            return mainNode;
        } catch (Exception e) {
            if (Objects.nonNull(processId)) terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getEnquiryPeriod(JsonNode jsonData, String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.replace("enquiryPeriod", jsonData.get("tender").get("enquiryPeriod"));
            return mainNode;
        } catch (Exception e) {
            if (Objects.nonNull(processId)) terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode addTenderTenderPeriod(final JsonNode jsonData,
                                          final JsonNode periodData,
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

    public JsonNode addTenderEnquiryPeriod(final JsonNode jsonData,
                                           final JsonNode periodData,
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

    public JsonNode addEnquiryWithAnswer(JsonNode jsonData, JsonNode enquiryData, String processId) {
        try {
            ((ObjectNode) jsonData).replace("enquiry", enquiryData.get("enquiry"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode addTenderStatus(final JsonNode jsonData, final JsonNode statusData, final String processId) {
        try {
            ObjectNode mainNode = ((ObjectNode) jsonData);
            mainNode.replace("tenderStatus", statusData.get("status"));
            mainNode.replace("tenderStatusDetails", statusData.get("statusDetails"));
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode addTenderUnsuspendData(JsonNode jsonData, JsonNode tenderData, String processId) {
        try {
            ((ObjectNode) jsonData).replace("tender", tenderData.get("tender"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getLots(JsonNode jsonData, String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.replace("lots", jsonData.get("lots"));
            return mainNode;
        } catch (Exception e) {
            if (Objects.nonNull(processId)) terminateProcess(processId, e.getMessage());
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

    public JsonNode addAccessFinalStatusesData(final JsonNode jsonData, final JsonNode lotsData, final String processId) {
        try {
            ((ObjectNode) jsonData).replace("lots", lotsData.get("lots"));
            ((ObjectNode) jsonData).replace("tender", lotsData.get("tender"));
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
            mainNode.replace("firstBids", awardData.get("firstBids"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode addOneAwardData(final JsonNode jsonData, final JsonNode awardData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("awardPeriod", awardData.get("awardPeriod"));
            mainNode.replace("award", awardData.get("award"));
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

    public JsonNode addCan(final JsonNode jsonData, final JsonNode cansData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("can", cansData.get("can"));
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
            if(!tenderNode.has("documents")) return null;

            final JsonNode documentsNode = tenderNode.get("documents");
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.set("documents", documentsNode);
            return mainNode;
        } catch (Exception e) {
            LOG.error("Error getting documents of tender.", e);
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public Optional<JsonNode> getDocumentsOfAmendmentsOfTender(final JsonNode jsonData, final String processId) {
        try {
            if (jsonData.has("amendments")) {
                final ArrayNode amendments = (ArrayNode) jsonData.get("amendments");
                final ArrayNode documents = jsonUtil.createArrayNode();
                for (JsonNode amendment : amendments) {
                    if (amendment.has("documents")) {
                        ((ArrayNode) amendment.get("documents"))
                                .forEach(documents::add);
                    }
                }

                if (documents.size() == 0)
                    return Optional.of(NullNode.getInstance());

                final ObjectNode mainNode = jsonUtil.createObjectNode();
                mainNode.set("documents", documents);
                return Optional.of(mainNode);
            } else return Optional.empty();

        } catch (Exception e) {
            LOG.error("Error getting documents of amendments of tender.", e);
            terminateProcess(processId, e.getMessage());
            return Optional.empty();
        }
    }

    public JsonNode setDocumentsOfAmendmentsOfTender(final JsonNode jsonData, final JsonNode documentsData, final String processId) {
        try {
            if (documentsData.has("documents")) {
                final ArrayNode documents = (ArrayNode) documentsData.get("documents");
                if (documents.size() > 0) {
                    final ObjectNode amendment = (ObjectNode) ((ArrayNode) jsonData.get("amendments")).get(0);
                    amendment.set("documents", documents);
                }
            }
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setDocumentsOfTender(final JsonNode jsonData, final JsonNode documentsData, final String processId) {
        try {
            final ObjectNode tenderNode = (ObjectNode) jsonData.get("tender");
            final ArrayNode documentsArray = (ArrayNode) documentsData.get("documents");
            if (documentsArray.size() > 0) {
                tenderNode.replace("documents", documentsArray);
            }
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getDocumentsOfAward(final JsonNode jsonData, final String processId) {
        try {
            final JsonNode documentsNode = jsonData.get("award").findPath("documents");
            if (documentsNode.isMissingNode()) return null;
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            final ArrayNode documentsArray = mainNode.putArray("documents");
            if (documentsNode.size() > 0) {
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

    public JsonNode getDocumentsOfCan(JsonNode jsonData, String processId) {
        try {
            final ArrayNode documentsArray = (ArrayNode) jsonData.get("contract").get("documents");
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.replace("documents", documentsArray);
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getDocumentsOfCanStorageValidate(JsonNode jsonData, String processId) {
        try {
            final ArrayNode documentsArray = (ArrayNode) jsonData.get("documents");
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.replace("documents", documentsArray);
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public Optional<JsonNode> getDocumentsOfCancelCanValidation(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode amendment = (ObjectNode) jsonData.get("contract").get("amendment");
            if (amendment.has("documents")) {
                final ObjectNode mainNode = jsonUtil.createObjectNode();
                final ArrayNode documents = (ArrayNode) amendment.get("documents");
                mainNode.replace("documents", documents);
                return Optional.of(mainNode);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return Optional.empty();
        }
    }

    public JsonNode getDocumentsOfCancelCanOpen(final JsonNode jsonData, final String processId) {
        try {
            final JsonNode cancelledCan = jsonData.get("cancelledCan");
            final JsonNode amendment = cancelledCan.get("amendment");
            final JsonNode documents = amendment.get("documents");
            if (documents != null) {
                final ObjectNode mainNode = jsonUtil.createObjectNode();
                mainNode.set("documents", documents);
                return mainNode;
            } else {
                return null;
            }
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setDocumentsOfCancelCanOpen(final JsonNode jsonData, final JsonNode documentsData, final String processId) {
        try {
            final JsonNode cancelledCan = jsonData.get("cancelledCan");
            final ObjectNode amendmentNode = (ObjectNode) cancelledCan.get("amendment");
            final ArrayNode documentsArray = (ArrayNode) documentsData.get("documents");
            if (documentsArray.size() > 0) {
                amendmentNode.replace("documents", documentsArray);
            }
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setDocumentsOfCan(JsonNode jsonData, JsonNode documentsData, String processId) {
        try {
            final ObjectNode contractNode = (ObjectNode) jsonData.get("contract");
            final ArrayNode documentsArray = (ArrayNode) documentsData.get("documents");
            if (documentsArray.size() > 0) {
                contractNode.replace("documents", documentsArray);
            }
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setDocumentsOfAward(final JsonNode jsonData, final JsonNode documentsData,
                                        final String processId) {
        try {
            final ObjectNode awardNode = (ObjectNode) jsonData.get("award");
            final ArrayNode documentsArray = (ArrayNode) documentsData.get("documents");
            if (documentsArray.size() > 0) {
                awardNode.replace("documents", documentsArray);
            }
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getDocumentsOfAcAward(JsonNode jsonData, String processId) {
        try {
            final ArrayNode documentsArray = (ArrayNode) jsonData.get("contractedAward").get("documents");
            if (documentsArray == null || documentsArray.size() < 1) return null;
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.replace("documents", documentsArray);
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setDocumentsOfAcAward(JsonNode jsonData, JsonNode documentsData, String processId) {
        try {
            final ObjectNode awardNode = (ObjectNode) jsonData.get("contractedAward");
            final ArrayNode documentsArray = (ArrayNode) documentsData.get("documents");
            if (documentsArray.size() > 0) {
                awardNode.replace("documents", documentsArray);
            }
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
                if (documentsNode != null && documentsNode.size() > 0) {
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

    public JsonNode getDocumentsOfBid(JsonNode jsonData, String processId) {
        try {
            final JsonNode documentsNode = jsonData.get("bid").get("documents");
            if (documentsNode == null) return null;
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            if (documentsNode.size() > 0) {
                mainNode.replace("documents", documentsNode);
            }
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setDocumentsOfBid(final JsonNode jsonData, final JsonNode documentsData,
                                      final String processId) {
        try {
            final ObjectNode bidNode = (ObjectNode) jsonData.get("bid");
            final ArrayNode documentsArray = (ArrayNode) documentsData.get("documents");
            if (documentsArray.size() > 0) {
                bidNode.replace("documents", documentsArray);
            }
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setDocumentsOfBids(final JsonNode jsonData, final JsonNode documentsData,
                                       final String processId) {
        try {
            final ArrayNode documentsArray = (ArrayNode) documentsData.get("documents");
            if (documentsArray != null) {
                ((ObjectNode) jsonData).replace("documents", documentsArray);
            }
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getDocumentsOfContractUpdate(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            final ArrayNode documentsArray = mainNode.putArray("documents");

            final ArrayNode docsOfAwardNode = (ArrayNode) jsonData.get("award").get("documents");
            if (docsOfAwardNode != null && docsOfAwardNode.size() > 0) {
                documentsArray.addAll(docsOfAwardNode);
            }
            final ArrayNode docsOfContractNode = (ArrayNode) jsonData.get("contract").get("documents");
            if (docsOfContractNode != null && docsOfContractNode.size() > 0) {
                documentsArray.addAll(docsOfContractNode);
            }
            final ArrayNode suppliersNode = (ArrayNode) jsonData.get("award").get("suppliers");
            if (suppliersNode != null && suppliersNode.size() > 0) {
                for (final JsonNode supplierNode : suppliersNode) {
                    final ArrayNode personesNode = (ArrayNode) supplierNode.get("persones");
                    if (personesNode != null && personesNode.size() > 0) {
                        for (final JsonNode personNode : personesNode) {
                            final ArrayNode bfsNode = (ArrayNode) personNode.get("businessFunctions");
                            if (bfsNode != null && bfsNode.size() > 0) {
                                for (final JsonNode bfNode : bfsNode) {
                                    final ArrayNode documentsOfBfNode = (ArrayNode) bfNode.get("documents");
                                    if (documentsOfBfNode != null && documentsOfBfNode.size() > 0) {
                                        documentsArray.addAll(documentsOfBfNode);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            final ArrayNode personesNode = (ArrayNode) jsonData.get("buyer").get("persones");
            if (personesNode != null && personesNode.size() > 0) {
                for (final JsonNode personNode : personesNode) {
                    final ArrayNode bfsNode = (ArrayNode) personNode.get("businessFunctions");
                    if (bfsNode != null && bfsNode.size() > 0) {
                        for (final JsonNode bfNode : bfsNode) {
                            final ArrayNode documentsOfBfNode = (ArrayNode) bfNode.get("documents");
                            if (documentsOfBfNode != null && documentsOfBfNode.size() > 0) {
                                documentsArray.addAll(documentsOfBfNode);
                            }
                        }
                    }
                }
            }
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getDocumentsOfContract(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            final ArrayNode documentsArray = mainNode.putArray("documents");
            final ArrayNode docsNode = (ArrayNode) jsonData.get("contract").get("documents");
            if (docsNode != null && docsNode.size() > 0) {
                for (final JsonNode docNode : docsNode) {
                    if (docNode.get("id") != null && !docNode.get("id").asText().isEmpty()) {
                        documentsArray.add(docNode);
                    }
                }
            }
            if (documentsArray.get(0) == null) {
                return null;
            } else {
                return mainNode;
            }
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setDocumentsOfContract(final JsonNode jsonData, final JsonNode documentsData, final String processId) {
        try {
            final ObjectNode contractsNode = (ObjectNode) jsonData.get("contract");
            final ArrayNode documentsArray = (ArrayNode) documentsData.get("documents");
            if (documentsArray.size() > 0) {
                contractsNode.replace("documents", documentsArray);
            }
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getDocumentsOfContractAward(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            final ArrayNode documentsArray = mainNode.putArray("documents");
            final ArrayNode docsOfAwardNode = (ArrayNode) jsonData.get("award").get("documents");
            if (docsOfAwardNode != null && docsOfAwardNode.size() > 0) {
                documentsArray.addAll(docsOfAwardNode);
            }
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setDocumentsOfContractAward(final JsonNode jsonData, final JsonNode documentsData, final String processId) {
        try {
            final ObjectNode awardsNode = (ObjectNode) jsonData.get("award");
            final ArrayNode documentsArray = (ArrayNode) documentsData.get("documents");
            if (documentsArray.size() > 0) {
                awardsNode.replace("documents", documentsArray);
            }
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getDocumentsOfContractPersones(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            final ArrayNode documentsArray = mainNode.putArray("documents");
            final ArrayNode suppliersNode = (ArrayNode) jsonData.get("award").get("suppliers");
            for (final JsonNode supplierNode : suppliersNode) {
                final ArrayNode personesNode = (ArrayNode) supplierNode.get("persones");
                for (final JsonNode personNode : personesNode) {
                    final ArrayNode bfsNode = (ArrayNode) personNode.get("businessFunctions");
                    for (final JsonNode bfNode : bfsNode) {
                        final ArrayNode documentsOfBfNode = (ArrayNode) bfNode.get("documents");
                        if (documentsOfBfNode != null && documentsOfBfNode.size() > 0) {
                            documentsArray.addAll(documentsOfBfNode);
                        }
                    }
                }
            }
            final ArrayNode personesNode = (ArrayNode) jsonData.get("buyer").get("persones");
            for (final JsonNode personNode : personesNode) {
                final ArrayNode bfsNode = (ArrayNode) personNode.get("businessFunctions");
                for (final JsonNode bfNode : bfsNode) {
                    final ArrayNode documentsOfBfNode = (ArrayNode) bfNode.get("documents");
                    if (documentsOfBfNode != null && documentsOfBfNode.size() > 0) {
                        documentsArray.addAll(documentsOfBfNode);
                    }
                }
            }
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setDocumentsOfContractPersones(final JsonNode jsonData, final JsonNode documentsData, final String processId) {
        try {
            final ArrayNode documentsArray = (ArrayNode) documentsData.get("documents");
            if (documentsArray != null) {
                ((ObjectNode) jsonData).replace("documentsOfContractPersones", documentsArray);
            }
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getDocumentsOfConsideredBid(final JsonNode jsonData, final String processId) {
        try {
            final JsonNode consideredBidNode = jsonData.get("consideredBid");
            if (consideredBidNode == null) return null;
            final JsonNode documentsNode = jsonData.get("consideredBid").findPath("documents");
            if (documentsNode.isMissingNode()) return null;
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            if (documentsNode.size() > 0) {
                mainNode.replace("documents", documentsNode);
            }
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setDocumentsOfConsideredBid(final JsonNode jsonData, final JsonNode documentsData, final String processId) {
        try {
            final ObjectNode bidNode = (ObjectNode) jsonData.get("consideredBid");
            final ArrayNode documentsArray = (ArrayNode) documentsData.get("documents");
            if (documentsArray.size() > 0) {
                bidNode.replace("documents", documentsArray);
            }
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getDocumentsOfConfirmationResponse(JsonNode jsonData, String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            final ArrayNode documentsArray = mainNode.putArray("documents");
            ObjectNode documentNode = jsonUtil.createObjectNode();
            final ArrayNode verificationNode = (ArrayNode) jsonData.get("confirmationResponse").get("value").get("verification");
            if (verificationNode != null && verificationNode.size() > 0) {
                final String value = verificationNode.get(0).get("value").asText();
                if (!value.isEmpty()) {
                    documentNode.put("id", value);
                    documentsArray.add(documentNode);
                } else {
                    return null;
                }
            }
            return mainNode;
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

    public JsonNode setTenderData(final Boolean itemsAdd, final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode tenderNode = (ObjectNode) jsonData.get("tender");
            final JsonNode tenderResponseNode = responseData.get("tender");
            if (itemsAdd) {
                tenderNode.replace("items", tenderResponseNode.get("items"));
                tenderNode.replace("classification", tenderResponseNode.get("classification"));
            }
            tenderNode.replace("lots", tenderResponseNode.get("lots"));
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

    public JsonNode getContractData(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.replace("items", jsonData.get("award").get("items"));
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setContractData(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode awardsNode = (ObjectNode) jsonData.get("award");
            awardsNode.replace("items", responseData.get("items"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getPlanning(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.replace("planning", jsonData.get("planning"));
            return mainNode;
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

    public JsonNode getAuctionData(final JsonNode prevData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            final JsonNode electronicAuctionsNode = prevData.get("tender").get("electronicAuctions");
            if (electronicAuctionsNode != null) {
                mainNode.replace("tenderPeriod", prevData.get("tender").get("tenderPeriod"));
                mainNode.replace("electronicAuctions", prevData.get("tender").get("electronicAuctions"));
            } else {
                return null;
            }
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setAuctionData(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode tenderNode = (ObjectNode) jsonData.get("tender");
            tenderNode.replace("auctionPeriod", responseData.get("auctionPeriod"));
            tenderNode.replace("electronicAuctions", responseData.get("electronicAuctions"));
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.put("isAuctionPeriodChanged", true);
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setAuctionStartData(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("isAuctionStarted", responseData.get("isAuctionStarted"));
            mainNode.replace("auctionsLinks", responseData.get("auctionsLinks"));
            mainNode.replace("electronicAuctions", responseData.get("electronicAuctions"));
            mainNode.replace("auctionsData", responseData.get("auctionsData"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getAuctionLaunchData(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            final JsonNode auctionsDataNode = jsonData.get("auctionsData");
            if (auctionsDataNode != null) {
                mainNode.replace("tender", auctionsDataNode.get("tender"));
                mainNode.replace("bids", auctionsDataNode.get("bids"));
            } else {
                return null;
            }
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setAuctionEndData(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("tender", responseData.get("tender"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode addContractTerm(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            ((ObjectNode) jsonData).replace("contractTerm", responseData.get("contractTerm"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode addActualBudgetSource(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("language", responseData.get("language"));
            mainNode.replace("actualBudgetSource", responseData.get("actualBudgetSource"));
            mainNode.replace("itemsCPVs", responseData.get("itemsCPVs"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getContractAwardValue(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            final ObjectNode awardsNode = mainNode.putObject("award");
            awardsNode.replace("id", jsonData.get("award").get("id"));
            awardsNode.replace("value", jsonData.get("award").get("value"));
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getDataForGetTerms(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            final ObjectNode awardsNode = mainNode.putObject("awards");
            awardsNode.replace("awards", jsonData.get("awards"));
            awardsNode.replace("contracts", jsonData.get("contracts"));
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getAgreedMetrics(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.replace("agreedMetrics", jsonData.get("contract").get("agreedMetrics"));
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setAgreedMetrics(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode contractsNode = (ObjectNode) jsonData.get("contract");
            contractsNode.replace("agreedMetrics", responseData.get("agreedMetrics"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setContractUpdateData(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("planning", responseData.get("planning"));
            mainNode.replace("award", responseData.get("award"));
            mainNode.replace("contract", responseData.get("contract"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setContractIssuedStatusDetails(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("contract", responseData.get("contract"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setContractFinalUpdateData(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("contract", responseData.get("contract"));
            mainNode.replace("approveBode", responseData.get("approveBody"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getCheckBs(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.replace("planning", jsonData.get("planning"));
            mainNode.replace("buyer", jsonData.get("buyer"));
            mainNode.replace("actualBudgetSource", jsonData.get("actualBudgetSource"));
            mainNode.replace("itemsCPVs", jsonData.get("itemsCPVs"));
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setCheckBs(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("treasuryBudgetSources", responseData.get("treasuryBudgetSources"));
            mainNode.replace("buyer", responseData.get("buyer"));
            mainNode.replace("funders", responseData.get("funders"));
            mainNode.replace("payers", responseData.get("payers"));
            mainNode.replace("addedEI", responseData.get("addedEI"));
            mainNode.replace("excludedEI", responseData.get("excludedEI"));
            mainNode.replace("addedFS", responseData.get("addedFS"));
            mainNode.replace("excludedFS", responseData.get("excludedFS"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getDataForBidUpdateStatus(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.replace("unsuccessfulLots", jsonData.get("unsuccessfulLots"));
            mainNode.replace("firstBids", jsonData.get("firstBids"));
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getConsideredBidId(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            final JsonNode consideredBidIdNode = jsonData.get("consideredBidId");
            if (consideredBidIdNode != null) {
                mainNode.replace("consideredBidId", jsonData.get("consideredBidId"));
                return mainNode;
            }
            return null;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setConsideredBid(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("consideredBid", responseData.get("consideredBid"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setRelatedBidsId(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("relatedBids", responseData.get("relatedBids"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getRelatedBidsData(JsonNode jsonData, String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.replace("relatedBids", jsonData.get("relatedBids"));
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getTreasuryValidationData(final JsonNode jsonData, final Context context, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.set("ocid", new TextNode(context.getOcid()));
            mainNode.set("cpid", new TextNode(context.getCpid()));
            mainNode.set("treasuryBudgetSources", jsonData.get("treasuryBudgetSources"));
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode setCompleteLotsData(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("tender", responseData.get("tender"));
            mainNode.replace("lots", responseData.get("lots"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

//    public JsonNode setAwardPeriod(final JsonNode jsonData, final JsonNode responseData, final String processId) {
//        try {
//            final ObjectNode mainNode = (ObjectNode) jsonData;
//            mainNode.replace("awardPeriod", responseData.get("awardPeriod"));
//
//            return jsonData;
//        } catch (Exception e) {
//            terminateProcess(processId, e.getMessage());
//            return null;
//        }
//    }

//    public JsonNode getCan(final JsonNode jsonData, final String processId) {
//        try {
//            final ObjectNode mainNode = jsonUtil.createObjectNode();
//            mainNode.replace("can", jsonData.get("can"));
//            return mainNode;
//        } catch (Exception e) {
//            terminateProcess(processId, e.getMessage());
//            return null;
//        }
//    }

    public JsonNode addCancelCan(JsonNode jsonData, JsonNode responseData, String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.remove("can");
            mainNode.set("cancelledCan", responseData.get("cancelledCan"));
            final JsonNode relatedCans = responseData.get("relatedCans");
            if (relatedCans != null) {
                mainNode.set("relatedCans", relatedCans);
            }
            mainNode.replace("contract", responseData.get("contract"));
            mainNode.replace("lotId", responseData.get("lotId"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getAwards(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.replace("awards", jsonData.get("awards"));
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode getLotId(final JsonNode jsonData, final String processId) {
        try {
            final ObjectNode mainNode = jsonUtil.createObjectNode();
            mainNode.replace("lotId", jsonData.get("lotId"));
            return mainNode;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode addLot(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("lot", responseData.get("lot"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode addInitialAwardsStatuses(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("awards", responseData.get("awards"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode addLotId(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("lotId", responseData.get("lotId"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode addCans(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("cans", responseData.get("cans"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode addContractedTender(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("contractedTender", responseData.get("contractedTender"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    public JsonNode addCreateAcData(final JsonNode jsonData, final JsonNode responseData, final String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("cans", responseData.get("cans"));
            mainNode.replace("contract", responseData.get("contract"));
            mainNode.replace("contractedAward", responseData.get("contractedAward"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }

    @Override
    public JsonNode addEvaluationFinalData(JsonNode jsonData, JsonNode responseData, String processId) {
        try {
            final ObjectNode mainNode = (ObjectNode) jsonData;
            mainNode.replace("awards", responseData.get("awards"));
            mainNode.replace("awardPeriod", responseData.get("awardPeriod"));
            return jsonData;
        } catch (Exception e) {
            terminateProcess(processId, e.getMessage());
            return null;
        }
    }
}