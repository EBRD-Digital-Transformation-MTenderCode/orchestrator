package com.procurement.orchestrator.controller;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Country;
import com.procurement.orchestrator.domain.Pmd;
import com.procurement.orchestrator.domain.Stage;
import com.procurement.orchestrator.rest.BudgetRestClient;
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
    private final BudgetRestClient budgetRestClient;

    public TenderController(final ProcessService processService,
                            final RequestService requestService,
                            final OperationService operationService,
                            final JsonUtil jsonUtil,
                            final DateUtil dateUtil,
                            final BudgetRestClient budgetRestClient) {
        super(jsonUtil, requestService, operationService);
        this.dateUtil = dateUtil;
        this.processService = processService;
        this.budgetRestClient = budgetRestClient;
    }

    @RequestMapping(value = "/cn", method = RequestMethod.POST)
    public ResponseEntity<String> createCN(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestParam("country") final String country,
                                           @RequestParam("pmd") final String pmd,
                                           @RequestBody final JsonNode jsonData) {
        final Context params = new Context();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        params.setEndDate(processService.getTenderPeriodEndDate(jsonData, null));
        params.setProcessType("createCN");
        params.setOperationType("createCN");
        params.setCountry(Country.fromValue(country.toUpperCase()).value());
        params.setPmd(Pmd.fromValue(pmd.toUpperCase()).value());
        params.setPhase("TENDERPERIOD");
        setStageForOperation(params);
        saveRequestAndCheckOperation(params, jsonData);
        processService.startProcess(params, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/cn/{identifier}", method = RequestMethod.POST)
    public ResponseEntity<String> updateCN(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestHeader("X-TOKEN") final String token,
                                           @PathVariable("identifier") final String identifier,
                                           @RequestParam("stage") final String stage,
                                           @RequestBody final JsonNode jsonData) {
        final Context params = new Context();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setCpid(identifier);
        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        params.setEndDate(processService.getTenderPeriodEndDate(jsonData, null));
        params.setNewStage(Stage.fromValue(stage).value());
        params.setProcessType("updateCN");
        params.setOperationType("updateCN");
        params.setToken(token);
        params.setPhase("TENDERPERIOD");
        saveRequestAndCheckOperation(params, jsonData);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", "updateCN");
        processService.startProcess(params, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/pin", method = RequestMethod.POST)
    public ResponseEntity<String> createPIN(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @RequestParam("country") final String country,
                                            @RequestParam("pmd") final String pmd,
                                            @RequestBody final JsonNode jsonData) {
        final Context params = new Context();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        params.setProcessType("createPIN");
        params.setOperationType("createPIN");
        params.setCountry(Country.fromValue(country.toUpperCase()).value());
        params.setPmd(Pmd.fromValue(pmd.toUpperCase()).value());
        params.setPhase("PLANNED");
        setStageForOperation(params);
        saveRequestAndCheckOperation(params, jsonData);
        processService.startProcess(params, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/pin/{identifier}", method = RequestMethod.POST)
    public ResponseEntity<String> updatePIN(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @RequestHeader("X-TOKEN") final String token,
                                            @PathVariable("identifier") final String identifier,
                                            @RequestParam("stage") final String stage,
                                            @RequestBody final JsonNode jsonData) {
        final Context params = new Context();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setCpid(identifier);
        params.setToken(token);
        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        params.setEndDate(processService.getTenderPeriodEndDate(jsonData, null));
        params.setNewStage(Stage.fromValue(stage).value());
        params.setProcessType("updatePIN");
        params.setOperationType("updatePIN");
        params.setPhase("PLANNED");
        saveRequestAndCheckOperation(params, jsonData);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", "updatePIN");
        processService.startProcess(params, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/pn", method = RequestMethod.POST)
    public ResponseEntity<String> createPN(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestParam("country") final String country,
                                           @RequestParam("pmd") final String pmd,
                                           @RequestBody final JsonNode jsonData) {
        final Context params = new Context();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        params.setProcessType("createPN");
        params.setOperationType("createPN");
        params.setCountry(Country.fromValue(country.toUpperCase()).value());
        params.setPmd(Pmd.fromValue(pmd.toUpperCase()).value());
        params.setPhase("PLANNING");
        setStageForOperation(params);
        saveRequestAndCheckOperation(params, jsonData);
        processService.startProcess(params, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/bid/{identifier}", method = RequestMethod.POST)
    public ResponseEntity<String> createBid(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @RequestHeader(value = "X-TOKEN", required = false) final String token,
                                            @PathVariable("identifier") final String identifier,
                                            @RequestParam("stage") final String stage,
                                            @RequestBody final JsonNode jsonData) {
        final Context params = new Context();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOperationId(operationId);
        params.setCpid(identifier);
        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        params.setEndDate(processService.getTenderPeriodEndDate(jsonData, null));
        params.setNewStage(Stage.fromValue(stage).value());
        params.setProcessType("submitTheBid");
        params.setOperationType("bid");
        params.setOwner(getOwner(authorization));
        params.setToken(token);
        saveRequestAndCheckOperation(params, jsonData);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("isTokenPresent", ((params.getToken() == null || "".equals(params.getToken().trim())) ? 0 : 1));
        processService.startProcess(params, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/enquiry/{identifier}", method = RequestMethod.POST)
    public ResponseEntity<String> enquiry(@RequestHeader("Authorization") final String authorization,
                                          @RequestHeader("X-OPERATION-ID") final String operationId,
                                          @RequestHeader(value = "X-TOKEN", required = false) final String token,
                                          @PathVariable("identifier") final String identifier,
                                          @RequestParam("stage") final String stage,
                                          @RequestBody final JsonNode jsonData) {
        final Context params = new Context();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setCpid(identifier);
        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        params.setNewStage(Stage.fromValue(stage).value());
        params.setProcessType("enquiry");
        params.setOperationType("createEnquiry");
        params.setToken(token);
        saveRequestAndCheckOperation(params, jsonData);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("isTokenPresent", (params.getToken() == null || "".equals(params.getToken().trim())) ? 0 : 1);
        variables.put("allAnswered", 0);
        processService.startProcess(params, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/award/{identifier}", method = RequestMethod.POST)
    public ResponseEntity<String> awardByBid(@RequestHeader("Authorization") final String authorization,
                                             @RequestHeader("X-OPERATION-ID") final String operationId,
                                             @RequestHeader("X-TOKEN") final String token,
                                             @PathVariable("identifier") final String identifier,
                                             @RequestParam("stage") final String stage,
                                             @RequestBody final JsonNode jsonData) {
        final Context params = new Context();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setCpid(identifier);
        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        params.setNewStage(Stage.fromValue(stage).value());
        final Map<String, Object> variables = new HashMap<>();
        if (params.getNewStage().equals(Stage.EV.value())) {
            variables.put("awardStatusDetails", "");
            params.setProcessType("awardByBidEv");
            params.setOperationType("awardByBidEv");
        } else {
            params.setProcessType("awardByBid");
            params.setOperationType("awardByBid");
        }
        params.setToken(token);
        saveRequestAndCheckOperation(params, jsonData);
        processService.startProcess(params, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/end-award-period/{identifier}", method = RequestMethod.POST)
    public ResponseEntity<String> endAwardPeriod(@RequestHeader("Authorization") final String authorization,
                                                 @RequestHeader("X-OPERATION-ID") final String operationId,
                                                 @PathVariable("identifier") final String identifier,
                                                 @RequestParam("stage") final String stage) {
        final Context params = new Context();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setCpid(identifier);
        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        params.setNewStage(Stage.fromValue(stage).value());
        final Map<String, Object> variables = new HashMap<>();
        if (params.getNewStage().equals(Stage.EV.value())) {
            params.setProcessType("awardPeriodEndEv");
            params.setOperationType("awardPeriodEndEv");
        } else {
            params.setProcessType("awardPeriodEnd");
            params.setOperationType("awardPeriodEnd");
        }
        params.setPhase("ENDSTAGE");
        saveRequestAndCheckOperation(params, null);
        processService.startProcess(params, variables);
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
        final Context params = new Context();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setCpid(identifier);
        params.setToken(token);
        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        params.setEndDate(dateUtil.format(endDate));
        params.setNewStage(Stage.fromValue(stage).value());
        params.setProcessType("startNewStage");
        params.setOperationType("startNewStage");
        params.setPhase("TENDERPERIOD");
        saveRequestAndCheckOperation(params, null);
        processService.startProcess(params, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/tenderPeriodEnd", method = RequestMethod.POST)
    public ResponseEntity<String> newStage(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestHeader("identifier") final String identifier,
                                           @RequestParam("stage") final String stage,
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                           @RequestParam("endDate") final LocalDateTime endDate) {
        final Context params = new Context();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setCpid(identifier);
        params.setNewStage(Stage.fromValue(stage).value());
        if (params.getNewStage().equals(Stage.EV.value())) {
            params.setProcessType("tenderPeriodEndEv");
            params.setOperationType("tenderPeriodEndEv");
        } else {
            params.setProcessType("tenderPeriodEnd");
            params.setOperationType("tenderPeriodEnd");
        }
        params.setCountry("TEST");
        params.setPmd("TEST_RT");
        params.setPhase("AWARDPERIOD");
        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        params.setEndDate(dateUtil.format(endDate));
        final Map<String, Object> variables = new HashMap<>();
        variables.put("checkEnquiries", 0);
        saveRequestAndCheckOperation(params, null);
        processService.startProcess(params, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

//    @RequestMapping(value = "/chronoTest", method = RequestMethod.POST)
//    public ResponseEntity<String> enquiry(@RequestHeader("Authorization") final String authorization,
//                                          @RequestHeader("X-OPERATION-ID") final String operationId,
//                                          @RequestParam("identifier") final String identifier,
//                                          @RequestParam("stage") final String stage) {
//        final Context params = new Context();
//        params.setRequestId(UUIDs.timeBased().toString());
//        params.setOwner(getOwner(authorization));
//        params.setOperationId(operationId);
//        params.setCpid(identifier);
//        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
//        params.setEndDate(dateUtil.format(dateUtil.localDateTimeNowUTC().plusMinutes(2)));
//        params.setNewStage(Stage.fromValue(stage).value());
//        params.setProcessType("chronoTest");
//        params.setOperationType("chronoTest");
//        saveRequestAndCheckOperation(params, null);
//        final Map<String, Object> variables = new HashMap<>();
//        processService.startProcess(params, variables);
//        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
//    }
//
//    @RequestMapping(value = "/test", method = RequestMethod.POST)
//    public ResponseEntity<String> test(@RequestBody final JsonNode jsonData) throws Exception{
//        final Context params = new Context();
//        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
//        final JsonNode checkFsDto = processService.getCheckFs(jsonData, params.getStartDate(), "");
//        final JsonNode responseData = processService.processResponse(
//                budgetRestClient.checkFs(checkFsDto),
//                params,
//                "",
//                "",
//                checkFsDto);
//        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
//    }

}

