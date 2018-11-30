package com.procurement.orchestrator.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.dao.CassandraDao;
import com.procurement.orchestrator.delegate.kafka.MessageProducer;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Notification;
import com.procurement.orchestrator.domain.Outcome;
import com.procurement.orchestrator.domain.Stage;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.NoticeRestClient;
import com.procurement.orchestrator.rest.RegulationRestClient;
import com.procurement.orchestrator.rest.StorageRestClient;
import com.procurement.orchestrator.service.NotificationService;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.Set;

import static com.procurement.orchestrator.domain.commands.NoticeCommandType.CREATE_RELEASE;
import static com.procurement.orchestrator.domain.commands.RegulationCommandType.GET_TERMS;
import static com.procurement.orchestrator.domain.commands.StorageCommandType.PUBLISH;

@RestController
@RequestMapping("/test")
public class TestController {


    private final RegulationRestClient regulationRestClient;
    private final StorageRestClient storageRestClient;
    private final NotificationService notificationService;
    private final NoticeRestClient noticeRestClient;
    private final MessageProducer messageProducer;


    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;
    private final DateUtil dateUtil;
    private final CassandraDao cassandraDao;

    public TestController(final OperationService operationService,
                          final ProcessService processService,
                          final JsonUtil jsonUtil,
                          final DateUtil dateUtil,
                          final CassandraDao cassandraDao,
                          final RegulationRestClient regulationRestClient,
                          final StorageRestClient storageRestClient,
                          final NotificationService notificationService,
                          final NoticeRestClient noticeRestClient,
                          final MessageProducer messageProducer) {
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
        this.cassandraDao = cassandraDao;
        this.regulationRestClient = regulationRestClient;
        this.storageRestClient = storageRestClient;
        this.notificationService = notificationService;
        this.noticeRestClient = noticeRestClient;
        this.messageProducer = messageProducer;
    }


    //processId = 2b324b1a-f42a-11e8-93bf-0242ac12000f
    //lastTaskId = ContractingCreateTask
    @RequestMapping(value = "/regulation/{processId}/{lastTaskId}", method = RequestMethod.POST)
    public ResponseEntity<String> regulation(@PathVariable("processId") final String processId,
                                             @PathVariable("lastTaskId") final String lastTaskId) throws Exception {

        String LAST_TASK = lastTaskId;

        OperationStepEntity entity = operationService.getOperationStep(processId, LAST_TASK);
        Context context = jsonUtil.toObject(Context.class, entity.getContext());
        JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        JsonNode rqData = processService.getDataForGetTerms(jsonData, processId);
//        LocalDateTime startDate = dateUtil.stringToLocal(context.getStartDate());
        if (rqData != null) {
            final JsonNode commandMessage = processService.getCommandMessage(GET_TERMS, context, jsonData);
            JsonNode responseData = processService.processResponse(
                    regulationRestClient.execute(commandMessage),
                    context,
                    processId,
                    "",
                    commandMessage);
            if (responseData != null) {
                JsonNode data = processService.addContractTerms(jsonData, responseData, processId);
                LAST_TASK = "RegulationGetContractTermsTask";
                entity.setTaskId(LAST_TASK);
                entity.setRequestData(jsonUtil.toJson(commandMessage));
                entity.setResponseData(jsonUtil.toJson(data));
                if (Objects.nonNull(context.getCpid()))
                    entity.setCpId(context.getCpid());
                cassandraDao.saveOperationStep(entity);

            }
        }
//*********************
        entity = operationService.getOperationStep(processId, LAST_TASK);
        jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        context = jsonUtil.toObject(Context.class, entity.getContext());
        final JsonNode documents = processService.getDocumentsOfAwards(jsonData, processId);
        if (Objects.nonNull(documents)) {
            final JsonNode commandMessage = processService.getCommandMessage(PUBLISH, context, documents);
            JsonNode responseData = processService.processResponse(
                    storageRestClient.execute(commandMessage),
                    context,
                    processId,
                    "",
                    commandMessage);
            if (Objects.nonNull(responseData)) {
                JsonNode data = processService.setDocumentsOfAwards(jsonData, responseData, processId);
                LAST_TASK = "StorageOpenDocsOfAwardsTask";
                entity.setTaskId(LAST_TASK);
                entity.setRequestData(jsonUtil.toJson(commandMessage));
                entity.setResponseData(jsonUtil.toJson(data));
                if (Objects.nonNull(context.getCpid()))
                    entity.setCpId(context.getCpid());
                cassandraDao.saveOperationStep(entity);
            }
        }
//*********************
        entity = operationService.getOperationStep(processId, LAST_TASK);
        context = jsonUtil.toObject(Context.class, entity.getContext());
        jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        final JsonNode commandMessage = processService.getCommandMessage(CREATE_RELEASE, context, jsonData);
        JsonNode responseData = null;
        if (Objects.nonNull(jsonData)) {
            responseData = processService.processResponse(
                    noticeRestClient.execute(commandMessage),
                    context,
                    processId,
                    "",
                    commandMessage);
        }
        if (Objects.nonNull(responseData)) {
            notificationService.addNoticeOutcomeToContext(context, responseData, processId);
            LAST_TASK = "NoticeCreateReleaseTask";
            entity.setTaskId(LAST_TASK);
            entity.setContext(jsonUtil.toJson(context));
            entity.setRequestData(jsonUtil.toJson(commandMessage));
            entity.setResponseData(jsonUtil.toJson(responseData));
            if (Objects.nonNull(context.getCpid()))
                entity.setCpId(context.getCpid());
            cassandraDao.saveOperationStep(entity);
        }
//*********************
        entity = operationService.getOperationStep(processId, LAST_TASK);
        context = jsonUtil.toObject(Context.class, entity.getContext());
        final Set<Outcome> contextOutcomes = context.getOutcomes();
        context.setPhase("awardedContractPreparation");
        operationService.saveContext(context);

        context.setStage(Stage.AC.value());
        context.setPhase("contractProject");
        for (final Outcome outcome : contextOutcomes) {
            if (outcome.getType().equals("ac")) {
                operationService.saveContractContext(outcome.getId(), context);
            }
        }

        LAST_TASK = "SaveContractContextTask";
        entity.setTaskId(LAST_TASK);
        entity.setContext(jsonUtil.toJson(context));
        entity.setRequestData(jsonUtil.toJson(commandMessage));
        entity.setResponseData(jsonUtil.toJson(responseData));
        if (Objects.nonNull(context.getCpid()))
            entity.setCpId(context.getCpid());
        cassandraDao.saveOperationStep(entity);
//*********************
        entity = operationService.getOperationStep(processId, LAST_TASK);
        context = jsonUtil.toObject(Context.class, entity.getContext());
        final Notification notification = notificationService.getNotificationForPlatform(context);
        if (notification != null) {
            messageProducer.sendToPlatform(notification);
            LAST_TASK = "SendMessageTask";
            entity.setTaskId(LAST_TASK);
            entity.setContext(jsonUtil.toJson(context));
            entity.setRequestData(jsonUtil.toJson(notification));
            if (Objects.nonNull(context.getCpid()))
                entity.setCpId(context.getCpid());
            cassandraDao.saveOperationStep(entity);
        }
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

}