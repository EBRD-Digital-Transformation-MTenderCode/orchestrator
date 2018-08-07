package com.procurement.orchestrator.controller;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Rules;
import com.procurement.orchestrator.domain.Stage;
import com.procurement.orchestrator.exception.OperationException;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.service.RequestService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TenderController extends BaseController {

    private final DateUtil dateUtil;
    private final ProcessService processService;
    private final OperationService operationService;
    private final String lang = "ro";

    public TenderController(final ProcessService processService,
                            final RequestService requestService,
                            final OperationService operationService,
                            final JsonUtil jsonUtil,
                            final DateUtil dateUtil) {
        super(jsonUtil, requestService, operationService);
        this.dateUtil = dateUtil;
        this.processService = processService;
        this.operationService = operationService;
    }

    @RequestMapping(value = "/cn", method = RequestMethod.POST)
    public ResponseEntity<String> createCN(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestParam("country") final String country,
                                           @RequestParam("pmd") final String pmd,
                                           @RequestBody final JsonNode data) {
        final Context context = getContextCreate(authorization, operationId, country, pmd, "createCN", data);
        saveRequestAndCheckOperation(context, data);
        processService.startProcess(context, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/cn/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> updateCN(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestHeader("X-TOKEN") final String token,
                                           @PathVariable("cpid") final String cpid,
                                           @PathVariable("ocid") final String ocid,
                                           @RequestBody final JsonNode data) {
        final Context context = getContextUpdate(authorization, operationId, cpid, ocid, token, "updateCN", data);
        saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", context.getOperationType());
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/pin", method = RequestMethod.POST)
    public ResponseEntity<String> createPIN(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @RequestParam("country") final String country,
                                            @RequestParam("pmd") final String pmd,
                                            @RequestBody final JsonNode data) {
        final Context context = getContextCreate(authorization, operationId, country, pmd, "createPIN", data);
        saveRequestAndCheckOperation(context, data);
        processService.startProcess(context, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/pin/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> updatePIN(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @RequestHeader("X-TOKEN") final String token,
                                            @PathVariable("cpid") final String cpid,
                                            @PathVariable("ocid") final String ocid,
                                            @RequestBody final JsonNode data) {
        final Context context = getContextUpdate(authorization, operationId, cpid, ocid, token, "updatePIN", data);
        saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", context.getOperationType());
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/pn", method = RequestMethod.POST)
    public ResponseEntity<String> createPN(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestParam("country") final String country,
                                           @RequestParam("pmd") final String pmd,
                                           @RequestBody final JsonNode data) {
        final Context context = getContextCreate(authorization, operationId, country, pmd, "createPN", data);
        saveRequestAndCheckOperation(context, data);
        processService.startProcess(context, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/bid/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> createBid(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @PathVariable("cpid") final String cpid,
                                            @PathVariable("ocid") final String ocid,
                                            @RequestBody final JsonNode data) {
        final Context context = getContextUpdate(authorization, operationId, cpid, ocid, null, "submitTheBid", data);
        saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("isTokenPresent", 0);
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/bid/{cpid}/{ocid}/{bidid}", method = RequestMethod.POST)
    public ResponseEntity<String> updateBid(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @RequestHeader(value = "X-TOKEN") final String token,
                                            @PathVariable("cpid") final String cpid,
                                            @PathVariable("ocid") final String ocid,
                                            @PathVariable("bidid") final String bidId,
                                            @RequestBody final JsonNode data) {
        final Context context = getContextUpdate(authorization, operationId, cpid, ocid, token, "submitTheBid", data);
        context.setBidId(bidId);
        saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("isTokenPresent", 1);
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/enquiry/{identifier}", method = RequestMethod.POST)
    public ResponseEntity<String> enquiry(@RequestHeader("Authorization") final String authorization,
                                          @RequestHeader("X-OPERATION-ID") final String operationId,
                                          @RequestHeader(value = "X-TOKEN", required = false) final String token,
                                          @PathVariable("identifier") final String identifier,
                                          @RequestParam("stage") final String stage,
                                          @RequestBody final JsonNode data) {
        final Context context = new Context();
        context.setRequestId(UUIDs.timeBased().toString());
        context.setOwner(getOwner(authorization));
        context.setOperationId(operationId);
        context.setCpid(identifier);
        context.setStartDate(dateUtil.nowFormatted());
        context.setStage(Stage.fromValue(stage).value());
        context.setProcessType("enquiry");
        context.setOperationType("createEnquiry");
        context.setLanguage(lang);
        context.setToken(token);
        saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("isTokenPresent", (context.getToken() == null || "".equals(context.getToken().trim())) ? 0 : 1);
        variables.put("allAnswered", 0);
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/award/{identifier}", method = RequestMethod.POST)
    public ResponseEntity<String> awardByBid(@RequestHeader("Authorization") final String authorization,
                                             @RequestHeader("X-OPERATION-ID") final String operationId,
                                             @RequestHeader("X-TOKEN") final String token,
                                             @PathVariable("identifier") final String identifier,
                                             @RequestParam("stage") final String stage,
                                             @RequestBody final JsonNode data) {
        final Context context = new Context();
        context.setRequestId(UUIDs.timeBased().toString());
        context.setOwner(getOwner(authorization));
        context.setOperationId(operationId);
        context.setCpid(identifier);
        context.setStartDate(dateUtil.nowFormatted());
        context.setStage(Stage.fromValue(stage).value());
        context.setLanguage(lang);
        final Map<String, Object> variables = new HashMap<>();
        if (context.getStage().equals(Stage.EV.value())) {
            variables.put("awardStatusDetails", "");
            context.setProcessType("awardByBidEv");
            context.setOperationType("awardByBidEv");
        } else {
            context.setProcessType("awardByBid");
            context.setOperationType("awardByBid");
        }
        context.setToken(token);
        saveRequestAndCheckOperation(context, data);
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/end-award-period/{identifier}", method = RequestMethod.POST)
    public ResponseEntity<String> endAwardPeriod(@RequestHeader("Authorization") final String authorization,
                                                 @RequestHeader("X-OPERATION-ID") final String operationId,
                                                 @PathVariable("identifier") final String identifier,
                                                 @RequestParam("stage") final String stage) {
        final Context context = new Context();
        context.setRequestId(UUIDs.timeBased().toString());
        context.setOwner(getOwner(authorization));
        context.setOperationId(operationId);
        context.setCpid(identifier);
        context.setStartDate(dateUtil.nowFormatted());
        context.setStage(Stage.fromValue(stage).value());
        final Map<String, Object> variables = new HashMap<>();
        context.setLanguage(lang);
        if (context.getStage().equals(Stage.EV.value())) {
            context.setProcessType("awardPeriodEndEv");
            context.setOperationType("awardPeriodEndEv");
        } else {
            context.setProcessType("awardPeriodEnd");
            context.setOperationType("awardPeriodEnd");
        }
        context.setPhase("ENDSTAGE");
        saveRequestAndCheckOperation(context, null);
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/new-stage/{identifier}", method = RequestMethod.POST)
    public ResponseEntity<String> newStage(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestHeader("X-TOKEN") final String token,
                                           @PathVariable("identifier") final String identifier,
                                           @RequestParam("stage") final String stage,
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                           @RequestParam("endDate") final LocalDateTime endDate) {
        final Context context = new Context();
        context.setRequestId(UUIDs.timeBased().toString());
        context.setOwner(getOwner(authorization));
        context.setOperationId(operationId);
        context.setCpid(identifier);
        context.setToken(token);
        context.setStartDate(dateUtil.nowFormatted());
        context.setEndDate(dateUtil.format(endDate));
        context.setStage(Stage.fromValue(stage).value());
        context.setProcessType("startNewStage");
        context.setOperationType("startNewStage");
        context.setLanguage(lang);
        context.setPhase("TENDERPERIOD");
        saveRequestAndCheckOperation(context, null);
        processService.startProcess(context, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }


    private Context getContextCreate(final String authorization,
                                     final String operationId,
                                     final String country,
                                     final String pmd,
                                     final String processType,
                                     final JsonNode data) {
        final Context context = new Context();
        final Rules rules = operationService.getRules(country, pmd, processType);
        context.setCountry(rules.getCountry());
        context.setPmd(rules.getPmd());
        context.setProcessType(rules.getProcessType());
        context.setStage(rules.getNewStage());
        context.setPhase(rules.getNewPhase());
        context.setOperationType(rules.getOperationType());

        context.setRequestId(UUIDs.timeBased().toString());
        context.setOwner(getOwner(authorization));
        context.setOperationId(operationId);
        context.setLanguage(lang);

        context.setStartDate(dateUtil.nowFormatted());
        context.setEndDate(processService.getTenderPeriodEndDate(data, null));

        return context;
    }

    private Context getContextUpdate(final String authorization,
                                     final String operationId,
                                     final String cpid,
                                     final String ocid,
                                     final String token,
                                     final String processType,
                                     final JsonNode data) {
        final Context prevContext = operationService.getContext(cpid);
        validateStageFromOcId(cpid, ocid, prevContext);
        final Rules rules = operationService.checkAndGetRules(prevContext, processType);
        final Context context = new Context();
        context.setCountry(rules.getCountry());
        context.setPmd(rules.getPmd());
        context.setProcessType(rules.getProcessType());
        context.setStage(rules.getNewStage());
        context.setPhase(rules.getNewPhase());
        context.setOperationType(rules.getOperationType());

        context.setRequestId(UUIDs.timeBased().toString());
        context.setOperationId(operationId);
        context.setOwner(getOwner(authorization));
        context.setCpid(cpid);
        context.setOcid(ocid);
        context.setToken(token);
        context.setLanguage(lang);

        context.setStartDate(dateUtil.nowFormatted());
        context.setEndDate(processService.getTenderPeriodEndDate(data, null));

        return context;
    }

    private void validateStageFromOcId(final String cpid, final String ocid, final Context prevContext) {
        String currentStage = null;
        for (final Stage stage : Stage.values()) {
            if (ocid.contains(stage.value())) {
                currentStage = stage.value();
            }
        }

        final String cpidFromOcid = ocid.substring(0, ocid.indexOf("-" + currentStage + "-"));
        if (!cpid.equals(cpidFromOcid)) {
            throw new OperationException("Invalid ocid.");
        }

        if (currentStage == null) {
            throw new OperationException("Invalid ocid.");
        } else {
            if (!currentStage.equals(prevContext.getStage())) {
                throw new OperationException("Invalid ocid.");
            }
        }
    }
}

