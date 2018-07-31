package com.procurement.orchestrator.controller;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Country;
import com.procurement.orchestrator.domain.Pmd;
import com.procurement.orchestrator.domain.Stage;
import com.procurement.orchestrator.exception.OperationException;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.service.RequestService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                                           @RequestBody final JsonNode jsonData) {
        final Context context = new Context();
        context.setRequestId(UUIDs.timeBased().toString());
        context.setOwner(getOwner(authorization));
        context.setOperationId(operationId);
        context.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        context.setEndDate(processService.getTenderPeriodEndDate(jsonData, null));
        context.setProcessType("createCN");
        context.setOperationType("createCN");
        context.setLanguage(lang);
        context.setCountry(Country.fromValue(country.toUpperCase()).value());
        context.setPmd(Pmd.fromValue(pmd.toUpperCase()).value());
        context.setPhase("TENDERPERIOD");
        context.setStage(getStageForOperation(context));
        saveRequestAndCheckOperation(context, jsonData);
        processService.startProcess(context, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/cn/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> updateCN(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestHeader("X-TOKEN") final String token,
                                           @PathVariable("cpid") final String cpid,
                                           @PathVariable("ocid") final String ocid,
                                           @RequestBody final JsonNode jsonData) {
        final Context context = new Context();
        context.setRequestId(UUIDs.timeBased().toString());
        context.setOwner(getOwner(authorization));
        context.setOperationId(operationId);
        context.setCpid(cpid);
        context.setOcid(ocid);
        context.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        context.setEndDate(processService.getTenderPeriodEndDate(jsonData, null));
        context.setProcessType("updateCN");
        context.setOperationType("updateCN");
        context.setLanguage(lang);
        context.setToken(token);
        context.setPhase("TENDERPERIOD");
        context.setStage(getStageForOperation(operationService.getContext(cpid)));
        saveRequestAndCheckOperation(context, jsonData);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", "updateCN");
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/pin", method = RequestMethod.POST)
    public ResponseEntity<String> createPIN(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @RequestParam("country") final String country,
                                            @RequestParam("pmd") final String pmd,
                                            @RequestBody final JsonNode jsonData) {
        final Context context = new Context();
        context.setRequestId(UUIDs.timeBased().toString());
        context.setOwner(getOwner(authorization));
        context.setOperationId(operationId);
        context.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        context.setProcessType("createPIN");
        context.setOperationType("createPIN");
        context.setLanguage(lang);
        context.setCountry(Country.fromValue(country.toUpperCase()).value());
        context.setPmd(Pmd.fromValue(pmd.toUpperCase()).value());
        context.setPhase("PLANNED");
        context.setStage(getStageForOperation(context));
        saveRequestAndCheckOperation(context, jsonData);
        processService.startProcess(context, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/pin/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> updatePIN(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @RequestHeader("X-TOKEN") final String token,
                                            @PathVariable("cpid") final String cpid,
                                            @PathVariable("ocid") final String ocid,
                                            @RequestBody final JsonNode jsonData) {
        final Context context = new Context();
        context.setRequestId(UUIDs.timeBased().toString());
        context.setOwner(getOwner(authorization));
        context.setOperationId(operationId);
        context.setCpid(cpid);
        context.setOcid(ocid);
        context.setToken(token);
        context.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        context.setEndDate(processService.getTenderPeriodEndDate(jsonData, null));
        context.setProcessType("updatePIN");
        context.setOperationType("updatePIN");
        context.setLanguage(lang);
        context.setPhase("PLANNED");
        context.setStage(getStageForOperation(operationService.getContext(cpid)));
        saveRequestAndCheckOperation(context, jsonData);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", "updatePIN");
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/pn", method = RequestMethod.POST)
    public ResponseEntity<String> createPN(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestParam("country") final String country,
                                           @RequestParam("pmd") final String pmd,
                                           @RequestBody final JsonNode jsonData) {
        final Context context = new Context();
        context.setRequestId(UUIDs.timeBased().toString());
        context.setOwner(getOwner(authorization));
        context.setOperationId(operationId);
        context.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        context.setProcessType("createPN");
        context.setOperationType("createPN");
        context.setLanguage(lang);
        context.setCountry(Country.fromValue(country.toUpperCase()).value());
        context.setPmd(Pmd.fromValue(pmd.toUpperCase()).value());
        context.setPhase("PLANNING");
        setStageForOperation(context);
        saveRequestAndCheckOperation(context, jsonData);
        processService.startProcess(context, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/bid/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> createBid(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @PathVariable("cpid") final String cpid,
                                            @PathVariable("ocid") final String ocid,
                                            @RequestBody final JsonNode jsonData) {
        final Context context = new Context();
        context.setRequestId(UUIDs.timeBased().toString());
        context.setOperationId(operationId);
        context.setCpid(cpid);
        context.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        context.setEndDate(processService.getTenderPeriodEndDate(jsonData, null));
        context.setProcessType("submitTheBid");
        context.setOperationType("bid");
        context.setLanguage(lang);
        context.setOwner(getOwner(authorization));
        final Stage stageFromOcid = getStageFromOcId(ocid);
        validateOcId(cpid, ocid, stageFromOcid);
        context.setStage(stageFromOcid.value());
        saveRequestAndCheckOperation(context, jsonData);
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
                                            @RequestBody final JsonNode jsonData) {
        final Context context = new Context();
        context.setRequestId(UUIDs.timeBased().toString());
        context.setOperationId(operationId);
        context.setCpid(cpid);
        context.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        context.setEndDate(processService.getTenderPeriodEndDate(jsonData, null));
        context.setProcessType("submitTheBid");
        context.setOperationType("bid");
        context.setLanguage(lang);
        context.setOwner(getOwner(authorization));
        context.setToken(token);
        context.setBidId(bidId);
        final Stage stageFromOcid = getStageFromOcId(ocid);
        validateOcId(cpid, ocid, stageFromOcid);
        context.setStage(stageFromOcid.value());
        saveRequestAndCheckOperation(context, jsonData);
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
                                          @RequestBody final JsonNode jsonData) {
        final Context context = new Context();
        context.setRequestId(UUIDs.timeBased().toString());
        context.setOwner(getOwner(authorization));
        context.setOperationId(operationId);
        context.setCpid(identifier);
        context.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        context.setStage(Stage.fromValue(stage).value());
        context.setProcessType("enquiry");
        context.setOperationType("createEnquiry");
        context.setLanguage(lang);
        context.setToken(token);
        saveRequestAndCheckOperation(context, jsonData);
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
                                             @RequestBody final JsonNode jsonData) {
        final Context context = new Context();
        context.setRequestId(UUIDs.timeBased().toString());
        context.setOwner(getOwner(authorization));
        context.setOperationId(operationId);
        context.setCpid(identifier);
        context.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
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
        saveRequestAndCheckOperation(context, jsonData);
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
        context.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
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
        context.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
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

    @RequestMapping(value = "/tenderPeriodEnd", method = RequestMethod.POST)
    public ResponseEntity<String> newStage(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestHeader("identifier") final String identifier,
                                           @RequestParam("stage") final String stage,
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                           @RequestParam("endDate") final LocalDateTime endDate) {
        final Context context = new Context();
        context.setRequestId(UUIDs.timeBased().toString());
        context.setOwner(getOwner(authorization));
        context.setOperationId(operationId);
        context.setCpid(identifier);
        context.setStage(Stage.fromValue(stage).value());
        context.setLanguage(lang);
        if (context.getStage().equals(Stage.EV.value())) {
            context.setProcessType("tenderPeriodEndEv");
            context.setOperationType("tenderPeriodEndEv");
        } else {
            context.setProcessType("tenderPeriodEnd");
            context.setOperationType("tenderPeriodEnd");
        }
        context.setCountry("TEST");
        context.setPmd("TEST_RT");
        context.setPhase("AWARDPERIOD");
        context.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        context.setEndDate(dateUtil.format(endDate));
        final Map<String, Object> variables = new HashMap<>();
        variables.put("checkEnquiries", 0);
        saveRequestAndCheckOperation(context, null);
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

//    @RequestMapping(value = "/chronoTest", method = RequestMethod.POST)
//    public ResponseEntity<String> enquiry(@RequestHeader("Authorization") final String authorization,
//                                          @RequestHeader("X-OPERATION-ID") final String operationId,
//                                          @RequestParam("identifier") final String identifier,
//                                          @RequestParam("stage") final String stage) {
//        final Context context = new Context();
//        context.setRequestId(UUIDs.timeBased().toString());
//        context.setOwner(getOwner(authorization));
//        context.setOperationId(operationId);
//        context.setCpid(identifier);
//        context.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
//        context.setEndDate(dateUtil.format(dateUtil.localDateTimeNowUTC().plusMinutes(2)));
//        context.setStage(Stage.fromValue(stage).value());
//        context.setProcessType("chronoTest");
//        context.setOperationType("chronoTest");
//        saveRequestAndCheckOperation(context, null);
//        final Map<String, Object> variables = new HashMap<>();
//        processService.startProcess(context, variables);
//        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
//    }
//
//    @RequestMapping(value = "/test", method = RequestMethod.POST)
//    public ResponseEntity<String> test(@RequestBody final JsonNode jsonData) throws Exception{
//        final Context context = new Context();
//        context.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
//        final JsonNode checkFsDto = processService.getCheckFs(jsonData, context.getStartDate(), "");
//        final JsonNode responseData = processService.processResponse(
//                budgetRestClient.checkFs(checkFsDto),
//                context,
//                "",
//                "",
//                checkFsDto);
//        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
//    }


    private Stage getStageFromOcId(final String ocid) {
        Stage currentStage = null;
        for (final Stage stage : Stage.values()) {
            if (ocid.contains(stage.value())) {
                currentStage = stage;
            }
        }
        if (currentStage == null) {
            throw new OperationException("Invalid ocid.");
        } else {
            return currentStage;
        }
    }


    private void validateOcId(final String cpid, final String ocid, final Stage stage) {
        final String cpidFromOcid = ocid.substring(0, ocid.indexOf("-" + stage.value() + "-"));
        if (!cpid.equals(cpidFromOcid)) {
            throw new OperationException("Invalid ocid.");
        }
    }

}

