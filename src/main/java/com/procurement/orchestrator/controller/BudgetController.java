package com.procurement.orchestrator.controller;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Country;
import com.procurement.orchestrator.domain.Stage;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.service.RequestService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BudgetController extends BaseController {

    private final DateUtil dateUtil;
    private final ProcessService processService;

    public BudgetController(final ProcessService processService,
                            final RequestService requestService,
                            final OperationService operationService,
                            final JsonUtil jsonUtil,
                            final DateUtil dateUtil) {
        super(jsonUtil, requestService, operationService);
        this.dateUtil = dateUtil;
        this.processService = processService;
    }

    @RequestMapping(value = "/ei", method = RequestMethod.POST)
    public ResponseEntity<String> createEIN(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @RequestParam("country") final String country,
                                            @RequestBody final JsonNode jsonData) {
        final Context context = new Context();
        context.setRequestId(UUIDs.timeBased().toString());
        context.setOwner(getOwner(authorization));
        context.setOperationId(operationId);
        context.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        context.setStage(Stage.EI.value());
        context.setCountry(Country.fromValue(country.toUpperCase()).value());
        context.setLanguage("en");
        context.setProcessType("ei");
        context.setOperationType("createEI");
        saveRequestAndCheckOperation(context, jsonData);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("isTokenPresent", 0);
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/ei/{cpid}", method = RequestMethod.POST)
    public ResponseEntity<String> updateEIN(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @RequestHeader("X-TOKEN") final String token,
                                            @PathVariable("cpid") final String cpid,
                                            @RequestBody final JsonNode jsonData) {
        final Context context = new Context();
        context.setRequestId(UUIDs.timeBased().toString());
        context.setOperationId(operationId);
        context.setCpid(cpid);
        context.setStage(Stage.EI.value());
        context.setProcessType("ei");
        context.setOperationType("updateEI");
        context.setOwner(getOwner(authorization));
        context.setToken(token);
        saveRequestAndCheckOperation(context, jsonData);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("isTokenPresent", (context.getToken() == null || "".equals(context.getToken().trim())) ? 0 : 1);
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/fs/{cpid}", method = RequestMethod.POST)
    public ResponseEntity<String> createFS(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @PathVariable("cpid") final String cpid,
                                           @RequestBody final JsonNode jsonData) {
        final Context context = new Context();
        context.setRequestId(UUIDs.timeBased().toString());
        context.setOperationId(operationId);
        context.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        context.setCpid(cpid);
        context.setStage(Stage.FS.value());
        context.setOperationType("createFS");
        context.setProcessType("fs");
        context.setOwner(getOwner(authorization));
        saveRequestAndCheckOperation(context, jsonData);
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
                                           @RequestBody final JsonNode jsonData) {
        final Context context = new Context();
        context.setRequestId(UUIDs.timeBased().toString());
        context.setOperationId(operationId);
        context.setCpid(cpid);
        context.setOcid(ocid);
        context.setStage(Stage.FS.value());
        context.setProcessType("fs");
        context.setOperationType("updateFS");
        context.setOwner(getOwner(authorization));
        context.setToken(token);
        saveRequestAndCheckOperation(context, jsonData);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("isTokenPresent", (context.getToken() == null || "".equals(context.getToken().trim())) ? 0 : 1);
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }
}

