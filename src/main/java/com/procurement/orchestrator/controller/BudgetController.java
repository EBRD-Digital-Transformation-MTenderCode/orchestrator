package com.procurement.orchestrator.controller;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Country;
import com.procurement.orchestrator.domain.Stage;
import com.procurement.orchestrator.exception.OperationException;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.service.RequestService;
import com.procurement.orchestrator.utils.DateUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class BudgetController extends DoBaseController {

    private final DateUtil dateUtil;
    private final ProcessService processService;
    private final RequestService requestService;
    private final String lang = "ro";

    public BudgetController(final ProcessService processService,
                            final RequestService requestService,
                            final DateUtil dateUtil) {
        this.dateUtil = dateUtil;
        this.processService = processService;
        this.requestService = requestService;
    }


    @RequestMapping(value = "/ei", method = RequestMethod.POST)
    public ResponseEntity<String> createEI(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestParam("country") final String country,
                                           @RequestParam(value = "testMode", defaultValue = "false") final boolean testMode,
                                           @RequestBody final JsonNode data) {
        requestService.validate(operationId, data);
        final Context context = new Context();
        context.setOperationId(operationId);
        context.setCountry(Country.fromValue(country.toUpperCase()).value());
        context.setLanguage(lang);
        context.setStage(Stage.EI.value());
        context.setOwner(requestService.getOwner(authorization));
        context.setProcessType("ei");
        context.setOperationType("createEI");
        context.setRequestId(UUIDs.timeBased().toString());
        context.setStartDate(dateUtil.nowFormatted());
        context.setTestMode(testMode);
        requestService.saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("isTokenPresent", 0);
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/ei/{cpid}", method = RequestMethod.POST)
    public ResponseEntity<String> updateEI(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestHeader("X-TOKEN") final String token,
                                           @PathVariable("cpid") final String cpid,
                                           @RequestBody final JsonNode data) {
        requestService.validate(operationId, data);
        final Context context = new Context();
        final Context prevContext = requestService.getContext(cpid);
        context.setCountry(prevContext.getCountry());
        context.setLanguage(lang);
        context.setOperationId(operationId);
        context.setCpid(cpid);
        context.setStage(Stage.EI.value());
        context.setProcessType("ei");
        context.setOperationType("updateEI");
        context.setOwner(requestService.getOwner(authorization));
        context.setToken(token);
        context.setRequestId(UUIDs.timeBased().toString());
        context.setStartDate(dateUtil.nowFormatted());
        requestService.saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("isTokenPresent", (context.getToken() == null || "".equals(context.getToken().trim())) ? 0 : 1);
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/fs/{cpid}", method = RequestMethod.POST)
    public ResponseEntity<String> createFS(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @PathVariable("cpid") final String cpid,
                                           @RequestBody final JsonNode data) {
        requestService.validate(operationId, data);
        final Context context = new Context();
        final Context prevContext = requestService.getContext(cpid);
        context.setCountry(prevContext.getCountry());
        context.setLanguage(lang);
        context.setOperationId(operationId);
        context.setCpid(cpid);
        context.setStage(Stage.FS.value());
        context.setProcessType("fs");
        context.setOperationType("createFS");
        context.setOwner(requestService.getOwner(authorization));
        context.setRequestId(UUIDs.timeBased().toString());
        context.setStartDate(dateUtil.nowFormatted());
        requestService.saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("isTokenPresent", 0);
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/fs/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> updateFS(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestHeader("X-TOKEN") final String token,
                                           @PathVariable("cpid") final String cpid,
                                           @PathVariable("ocid") final String ocid,
                                           @RequestBody final JsonNode data) {
        requestService.validate(operationId, data);
        validateOcId(cpid, ocid);
        final Context context = new Context();
        final Context prevContext = requestService.getContext(cpid);
        context.setCountry(prevContext.getCountry());
        context.setLanguage(lang);
        context.setOperationId(operationId);
        context.setCpid(cpid);
        context.setOcid(ocid);
        context.setStage(Stage.FS.value());
        context.setProcessType("fs");
        context.setOperationType("updateFS");
        context.setOwner(requestService.getOwner(authorization));
        context.setToken(token);
        context.setRequestId(UUIDs.timeBased().toString());
        context.setStartDate(dateUtil.nowFormatted());
        requestService.saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("isTokenPresent", (context.getToken() == null || "".equals(context.getToken().trim())) ? 0 : 1);
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    private void validateOcId(final String cpid, final String ocid) {
        final String cpidFromOcid = ocid.substring(0, ocid.indexOf("-FS-"));
        if (!cpid.equals(cpidFromOcid)) {
            throw new OperationException("Invalid ocid.");
        }
    }

}

