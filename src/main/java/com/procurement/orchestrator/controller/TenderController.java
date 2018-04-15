package com.procurement.orchestrator.controller;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.RequestService;
import com.procurement.orchestrator.domain.Country;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.domain.Stage;
import com.procurement.orchestrator.service.ProcessService;
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

    public TenderController(final ProcessService processService,
                            final RequestService requestService,
                            final OperationService operationService,
                            final JsonUtil jsonUtil,
                            final DateUtil dateUtil) {
        super(jsonUtil, requestService, operationService);
        this.dateUtil = dateUtil;
        this.processService = processService;
    }

    @RequestMapping(value = "/cn", method = RequestMethod.POST)
    public ResponseEntity<String> createCN(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestParam("country") final String country,
                                           @RequestParam("pmd") final String pmd,
                                           @RequestBody final JsonNode jsonData) {
        final Params params = new Params();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        params.setEndDate(processService.getTenderPeriodEndDate(jsonData, null));
        params.setNewStage(Stage.PS.value());
        params.setProcessType("createCN");
        params.setOperationType("createCN");
        params.setCountry(Country.fromValue(country.toUpperCase()).value());
        params.setPmd(pmd.toUpperCase());
        saveRequestAndCheckOperation(params, jsonData);
        processService.startProcess(params, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/cn/{identifier}", method = RequestMethod.POST)
    public ResponseEntity<String> updateCN(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @PathVariable("identifier") final String identifier,
                                           @RequestParam("country") final String country,
                                           @RequestParam("pmd") final String pmd,
                                           @RequestBody final JsonNode jsonData) {
        final Params params = new Params();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setCpid(identifier);
        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        params.setProcessType("updateCN");
        params.setOperationType("updateCN");
        params.setCountry(Country.fromValue(country.toUpperCase()).value());
        params.setPmd(pmd.toUpperCase());
        saveRequestAndCheckOperation(params, jsonData);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("isTokenPresent", ((params.getToken() == null || "".equals(params.getToken().trim())) ? 0 : 1));
        processService.startProcess(params, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/pin", method = RequestMethod.POST)
    public ResponseEntity<String> createPIN(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @RequestParam("country") final String country,
                                            @RequestParam("pmd") final String pmd,
                                            @RequestBody final JsonNode jsonData) {
        final Params params = new Params();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        params.setNewStage(Stage.PIN.value());
        params.setProcessType("createPIN");
        params.setOperationType("createPIN");
        params.setCountry(Country.fromValue(country.toUpperCase()).value());
        params.setPmd(pmd.toUpperCase());
        saveRequestAndCheckOperation(params, jsonData);
        processService.startProcess(params, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/pin/{identifier}", method = RequestMethod.POST)
    public ResponseEntity<String> updatePIN(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @RequestHeader("X-TOKEN") final String token,
                                            @PathVariable("identifier") final String identifier,
                                            @RequestBody final JsonNode jsonData) {
        final Params params = new Params();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setCpid(identifier);
        params.setToken(token);
        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        params.setNewStage(Stage.PIN.value());
        params.setProcessType("updatePIN");
        params.setOperationType("updatePIN");
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
        final Params params = new Params();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        params.setNewStage(Stage.PN.value());
        params.setProcessType("createPN");
        params.setOperationType("createPN");
        params.setCountry(Country.fromValue(country.toUpperCase()).value());
        params.setPmd(pmd.toUpperCase());
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
        final Params params = new Params();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOperationId(operationId);
        params.setCpid(identifier);
        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
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
                                          @RequestParam("country") final String country,
                                          @RequestParam("pmd") final String pmd,
                                          @RequestParam("stage") final String stage,
                                          @RequestBody final JsonNode jsonData) {
        final Params params = new Params();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setCpid(identifier);
        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        params.setNewStage(Stage.fromValue(stage).value());
        params.setProcessType("enquiry");
        params.setOperationType("createEnquiry");
        params.setCountry(Country.fromValue(country.toUpperCase()).value());
        params.setPmd(pmd.toUpperCase());
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
                                             @RequestHeader(value = "X-TOKEN", required = false) final String token,
                                             @PathVariable("identifier") final String identifier,
                                             @RequestParam("stage") final String stage,
                                             @RequestBody final JsonNode jsonData) {
        final Params params = new Params();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setCpid(identifier);
        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        params.setNewStage(Stage.fromValue(stage).value());
        params.setProcessType("awardByBid");
        params.setOperationType("awardByBid");
        params.setToken(token);
        saveRequestAndCheckOperation(params, jsonData);
        processService.startProcess(params, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/endAwardPeriod/{identifier}", method = RequestMethod.POST)
    public ResponseEntity<String> endAwardPeriod(@RequestHeader("Authorization") final String authorization,
                                                 @RequestHeader("X-OPERATION-ID") final String operationId,
                                                 @PathVariable("identifier") final String identifier,
                                                 @RequestParam("stage") final String stage,
                                                 @RequestParam("country") final String country,
                                                 @RequestParam("pmd") final String pmd) {
        final Params params = new Params();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setCpid(identifier);
        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        params.setNewStage(Stage.fromValue(stage).value());
        params.setCountry(Country.fromValue(country.toUpperCase()).value());
        params.setPmd(pmd.toUpperCase());
        params.setProcessType("awardPeriodEnd");
        params.setOperationType("awardPeriodEnd");
        saveRequestAndCheckOperation(params, null);
        processService.startProcess(params, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/newStage/{identifier}", method = RequestMethod.POST)
    public ResponseEntity<String> newStage(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestHeader("X-TOKEN") final String token,
                                           @PathVariable("identifier") final String identifier,
                                           @RequestParam("stage") final String stage,
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                           @RequestParam("endDate") final LocalDateTime endDate) {
        final Params params = new Params();
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
        saveRequestAndCheckOperation(params, null);
        processService.startProcess(params, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }
}

