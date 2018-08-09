package com.procurement.orchestrator.controller;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Rule;
import com.procurement.orchestrator.domain.Stage;
import com.procurement.orchestrator.exception.OperationException;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.service.RequestService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.UUID;
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
                                           @RequestHeader("X-OPERATION-ID") final UUID operationId,
                                           @RequestParam("country") final String country,
                                           @RequestParam("pmd") final String pmd,
                                           @RequestBody final JsonNode data) {
        final Context context = getContextCreate(authorization, operationId.toString(), country, pmd, "createCN");
        context.setEndDate(processService.getTenderPeriodEndDate(data, null));
        saveRequestAndCheckOperation(context, data);
        processService.startProcess(context, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/cn/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> updateCN(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final UUID operationId,
                                           @RequestHeader("X-TOKEN") final String token,
                                           @PathVariable("cpid") final String cpid,
                                           @PathVariable("ocid") final String ocid,
                                           @RequestBody final JsonNode data) {
        final Context context = getContext(authorization, operationId.toString(), cpid, ocid, token, "updateCN");
        context.setEndDate(processService.getTenderPeriodEndDate(data, null));
        saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", context.getOperationType());
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/pin", method = RequestMethod.POST)
    public ResponseEntity<String> createPIN(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final UUID operationId,
                                            @RequestParam("country") final String country,
                                            @RequestParam("pmd") final String pmd,
                                            @RequestBody final JsonNode data) {
        final Context context = getContextCreate(authorization, operationId.toString(), country, pmd, "createPIN");
        context.setEndDate(processService.getTenderPeriodEndDate(data, null));
        saveRequestAndCheckOperation(context, data);
        processService.startProcess(context, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/pin/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> updatePIN(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final UUID operationId,
                                            @RequestHeader("X-TOKEN") final String token,
                                            @PathVariable("cpid") final String cpid,
                                            @PathVariable("ocid") final String ocid,
                                            @RequestBody final JsonNode data) {
        final Context context = getContext(authorization, operationId.toString(), cpid, ocid, token, "updatePIN");
        context.setEndDate(processService.getTenderPeriodEndDate(data, null));
        saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", context.getOperationType());
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/pn", method = RequestMethod.POST)
    public ResponseEntity<String> createPN(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final UUID operationId,
                                           @RequestParam("country") final String country,
                                           @RequestParam("pmd") final String pmd,
                                           @RequestBody final JsonNode data) {
        final Context context = getContextCreate(authorization, operationId.toString(), country, pmd, "createPN");
        context.setEndDate(processService.getTenderPeriodEndDate(data, null));
        saveRequestAndCheckOperation(context, data);
        processService.startProcess(context, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/bid/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> createBid(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final UUID operationId,
                                            @PathVariable("cpid") final String cpid,
                                            @PathVariable("ocid") final String ocid,
                                            @RequestBody final JsonNode data) {
        final Context context = getContext(authorization, operationId.toString(), cpid, ocid, null, "submitTheBid");
        saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("isTokenPresent", 0);
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/bid/{cpid}/{ocid}/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> updateBid(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final UUID operationId,
                                            @RequestHeader("X-TOKEN") final String token,
                                            @PathVariable("cpid") final String cpid,
                                            @PathVariable("ocid") final String ocid,
                                            @PathVariable("id") final UUID id,
                                            @RequestBody final JsonNode data) {
        final Context context = getContext(authorization, operationId.toString(), cpid, ocid, token, "submitTheBid");
        context.setId(id.toString());
        saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("isTokenPresent", 1);
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/enquiry/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> createEnquiry(@RequestHeader("Authorization") final String authorization,
                                                @RequestHeader("X-OPERATION-ID") final UUID operationId,
                                                @PathVariable("cpid") final String cpid,
                                                @PathVariable("ocid") final String ocid,
                                                @RequestBody final JsonNode data) {
        final Context context = getContext(authorization, operationId.toString(), cpid, ocid, null, "enquiry");
        saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("isTokenPresent", 0);
        variables.put("allAnswered", 0);
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/enquiry/{cpid}/{ocid}/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> updateEnquiry(@RequestHeader("Authorization") final String authorization,
                                                @RequestHeader("X-OPERATION-ID") final UUID operationId,
                                                @RequestHeader("X-TOKEN") final String token,
                                                @PathVariable("cpid") final String cpid,
                                                @PathVariable("ocid") final String ocid,
                                                @PathVariable("id") final UUID id,
                                                @RequestBody final JsonNode data) {
        final Context context = getContext(authorization, operationId.toString(), cpid, ocid, token, "enquiry");
        context.setId(id.toString());
        saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("isTokenPresent", 1);
        variables.put("allAnswered", 0);
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/award/{cpid}/{ocid}/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> awardByBid(@RequestHeader("Authorization") final String authorization,
                                             @RequestHeader("X-OPERATION-ID") final UUID operationId,
                                             @RequestHeader("X-TOKEN") final String token,
                                             @PathVariable("cpid") final String cpid,
                                             @PathVariable("ocid") final String ocid,
                                             @PathVariable("id") final UUID id,
                                             @RequestBody final JsonNode data) {
        final Context context = getContext(authorization, operationId.toString(), cpid, ocid, token, "awardByBid");
        context.setId(id.toString());
        saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", context.getOperationType());
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/end-award-period/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> endAwardPeriod(@RequestHeader("Authorization") final String authorization,
                                                 @RequestHeader("X-OPERATION-ID") final UUID operationId,
                                                 @PathVariable("cpid") final String cpid,
                                                 @PathVariable("ocid") final String ocid) {
        final Context context = getContext(authorization, operationId.toString(), cpid, ocid, null, "awardPeriodEnd");
        saveRequestAndCheckOperation(context, null);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", context.getOperationType());
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/new-stage/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> newStage(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final UUID operationId,
                                           @RequestHeader("X-TOKEN") final String token,
                                           @PathVariable("cpid") final String cpid,
                                           @PathVariable("ocid") final String ocid,
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                           @RequestParam("endDate") final LocalDateTime endDate) {
        final Context context = getContext(authorization, operationId.toString(), cpid, ocid, token, "startNewStage");
        context.setEndDate(dateUtil.format(endDate));
        saveRequestAndCheckOperation(context, null);
        processService.startProcess(context, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }


    private Context getContextCreate(final String authorization,
                                     final String operationId,
                                     final String country,
                                     final String pmd,
                                     final String processType) {
        final Context context = new Context();
        final Rule rules = operationService.getRule(country, pmd, processType);
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
        return context;
    }

    private Context getContext(final String authorization,
                               final String operationId,
                               final String cpid,
                               final String ocid,
                               final String token,
                               final String processType) {
        final Context prevContext = operationService.getContext(cpid);
        validateStageFromOcId(cpid, ocid, prevContext);
        final Rule rule = operationService.checkAndGetRule(prevContext, processType);
        final Context context = new Context();
        context.setCountry(rule.getCountry());
        context.setPmd(rule.getPmd());
        context.setProcessType(rule.getProcessType());
        context.setStage(rule.getNewStage());
        context.setPhase(rule.getNewPhase());
        context.setOperationType(rule.getOperationType());

        context.setRequestId(UUIDs.timeBased().toString());
        context.setOperationId(operationId);
        context.setOwner(getOwner(authorization));
        context.setCpid(cpid);
        context.setOcid(ocid);
        context.setToken(token);
        context.setLanguage(lang);

        context.setStartDate(dateUtil.nowFormatted());

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

