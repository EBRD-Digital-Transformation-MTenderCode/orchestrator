package com.procurement.orchestrator.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
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
public class TenderController extends DoBaseController {

    private final DateUtil dateUtil;
    private final ProcessService processService;
    private final RequestService requestService;
    private final JsonUtil jsonUtil;

    public TenderController(final ProcessService processService,
                            final RequestService requestService,
                            final JsonUtil jsonUtil,
                            final DateUtil dateUtil) {
        this.dateUtil = dateUtil;
        this.jsonUtil = jsonUtil;
        this.processService = processService;
        this.requestService = requestService;
    }

    @RequestMapping(value = "/cn", method = RequestMethod.POST)
    public ResponseEntity<String> createCN(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestParam("country") final String country,
                                           @RequestParam("pmd") final String pmd,
                                           @RequestBody final JsonNode data) {
        requestService.validate(operationId, data);
        final Context context = requestService.getContextForCreate(authorization, operationId, country, pmd, "createCN");
        processService.setEnquiryPeriodStartDate(data, context.getStartDate(), null);
        processService.setTenderPeriodStartDate(data, processService.getEnquiryPeriodEndDate(data, null), null);
        requestService.saveRequestAndCheckOperation(context, data);
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
        requestService.validate(operationId, data);
        final Context context = requestService.getContextForUpdate(authorization, operationId, cpid, ocid, token, "updateCN");
        processService.setEnquiryPeriodStartDate(data, context.getStartDate(), null);
        processService.setTenderPeriodStartDate(data, processService.getEnquiryPeriodEndDate(data, null), null);
        requestService.saveRequestAndCheckOperation(context, data);
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
        requestService.validate(operationId, data);
        final Context context = requestService.getContextForCreate(authorization, operationId, country, pmd, "createPIN");
        context.setEndDate(processService.getTenderPeriodEndDate(data, null));
        requestService.saveRequestAndCheckOperation(context, data);
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
        requestService.validate(operationId, data);
        final Context context = requestService.getContextForUpdate(authorization, operationId, cpid, ocid, token, "updatePIN");
        context.setEndDate(processService.getTenderPeriodEndDate(data, null));
        requestService.saveRequestAndCheckOperation(context, data);
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
        requestService.validate(operationId, data);
        final Context context = requestService.getContextForCreate(authorization, operationId, country, pmd, "createPN");
        context.setEndDate(processService.getTenderPeriodEndDate(data, null));
        requestService.saveRequestAndCheckOperation(context, data);
        processService.startProcess(context, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/pn/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> updatePN(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestHeader("X-TOKEN") final String token,
                                           @PathVariable("cpid") final String cpid,
                                           @PathVariable("ocid") final String ocid,
                                           @RequestBody final JsonNode data) {
        requestService.validate(operationId, data);
        final Context context = requestService.getContextForUpdate(authorization, operationId, cpid, ocid, token, "updatePN");
        context.setEndDate(processService.getTenderPeriodEndDate(data, null));
        requestService.saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", context.getOperationType());
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/bid/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> createBid(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @PathVariable("cpid") final String cpid,
                                            @PathVariable("ocid") final String ocid,
                                            @RequestBody final JsonNode data) {
        requestService.validate(operationId, data);
        final Context context = requestService.getContextForUpdate(authorization, operationId, cpid, ocid, null, "createBid");
        context.setOperationType("createBid");
        requestService.saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", "createBid");
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/bid/{cpid}/{ocid}/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> updateBid(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @RequestHeader("X-TOKEN") final String token,
                                            @PathVariable("cpid") final String cpid,
                                            @PathVariable("ocid") final String ocid,
                                            @PathVariable("id") final String id,
                                            @RequestBody final JsonNode data) {
        requestService.validate(operationId, data);
        final Context context = requestService.getContextForUpdate(authorization, operationId, cpid, ocid, token, "updateBid");
        context.setId(id);
        context.setOperationType("updateBid");
        requestService.saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", "updateBid");
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/enquiry/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> createEnquiry(@RequestHeader("Authorization") final String authorization,
                                                @RequestHeader("X-OPERATION-ID") final String operationId,
                                                @PathVariable("cpid") final String cpid,
                                                @PathVariable("ocid") final String ocid,
                                                @RequestBody final JsonNode data) {
        requestService.validate(operationId, data);
        final Context context = requestService.getContextForUpdate(authorization, operationId, cpid, ocid, null, "enquiry");
        requestService.saveRequestAndCheckOperation(context, data);
        processService.startProcess(context, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/enquiry/{cpid}/{ocid}/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> addAnswer(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @RequestHeader("X-TOKEN") final String token,
                                            @PathVariable("cpid") final String cpid,
                                            @PathVariable("ocid") final String ocid,
                                            @PathVariable("id") final String id,
                                            @RequestBody final JsonNode data) {
        requestService.validate(operationId, data);
        final Context context = requestService.getContextForUpdate(authorization, operationId, cpid, ocid, token, "answer");
        context.setId(id);
        requestService.saveRequestAndCheckOperation(context, data);
        processService.startProcess(context, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/award/{cpid}/{ocid}/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> awardByBid(@RequestHeader("Authorization") final String authorization,
                                             @RequestHeader("X-OPERATION-ID") final String operationId,
                                             @RequestHeader("X-TOKEN") final String token,
                                             @PathVariable("cpid") final String cpid,
                                             @PathVariable("ocid") final String ocid,
                                             @PathVariable("id") final String id,
                                             @RequestBody final JsonNode data) {
        requestService.validate(operationId, data);
        final Context context = requestService.getContextForUpdate(authorization, operationId, cpid, ocid, token, "awardByBid");
        context.setId(id);
        requestService.saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", context.getOperationType());
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/protocol/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> endAwardPeriod(@RequestHeader("Authorization") final String authorization,
                                                 @RequestHeader("X-OPERATION-ID") final String operationId,
                                                 @RequestHeader("X-TOKEN") final String token,
                                                 @PathVariable("cpid") final String cpid,
                                                 @PathVariable("ocid") final String ocid) {
        requestService.validate(operationId, null);
        final Context context = requestService.getContextForUpdate(authorization, operationId, cpid, ocid, token, "awardPeriodEnd");
        requestService.saveRequestAndCheckOperation(context, null);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", context.getOperationType());
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/new-stage/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> newStage(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestHeader("X-TOKEN") final String token,
                                           @PathVariable("cpid") final String cpid,
                                           @PathVariable("ocid") final String ocid,
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                           @RequestParam("endDate") final LocalDateTime endDate) {
        requestService.validate(operationId, null);
        final Context context = requestService.getContextForUpdate(authorization, operationId, cpid, ocid, token, "startNewStage");
        context.setEndDate(dateUtil.format(endDate));
        requestService.saveRequestAndCheckOperation(context, null);
        processService.startProcess(context, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

//    @RequestMapping(value = "/auctionPeriodEnd", method = RequestMethod.POST)
//    public ResponseEntity<String> test(@RequestBody final JsonNode response) {
//        final JsonNode dataNode = response.get("data");
//        final String cpid = dataNode.get("tender").get("id").asText();
//        final Context prevContext = requestService.getContext(cpid);
//        final Context context = new Context();
//        final String uuid = UUIDs.timeBased().toString();
//        context.setRequestId(uuid);
//        context.setOperationId(uuid);
//        context.setCountry("MD");
//        context.setPmd("OT");
//        context.setProcessType("auctionPeriodEnd");
//        context.setStage("EV");
//        context.setPhase("awarding");
//        context.setOperationType("auctionPeriodEnd");
//        //JSON parse error
//        context.setOwner(prevContext.getOwner());
//        context.setCpid(prevContext.getCpid());
//        context.setOcid(prevContext.getOcid());
//        context.setToken(prevContext.getToken());
//        context.setLanguage(prevContext.getLanguage());
//        context.setIsAuction(prevContext.getIsAuction());
//        context.setStartDate(dateUtil.nowFormatted());
//        requestService.saveRequestAndCheckOperation(context, dataNode);
//        final Map<String, Object> variables = new HashMap<>();
//        variables.put("operationType", context.getOperationType());
//        processService.startProcess(context, variables);
//        return new ResponseEntity<>(dataNode.toString(), HttpStatus.ACCEPTED);
//    }

}