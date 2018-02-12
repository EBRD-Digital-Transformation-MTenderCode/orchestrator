package com.procurement.orchestrator.controller;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.cassandra.service.RequestService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BudgetController extends BaseController {

    public BudgetController(final ProcessService processService,
                            final RequestService requestService,
                            final OperationService operationService,
                            final JsonUtil jsonUtil) {
        super(jsonUtil, requestService, operationService, processService);
    }

    @RequestMapping(value = "/ei", method = RequestMethod.POST)
    public ResponseEntity<String> createEIN(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @RequestBody final JsonNode jsonData) {
        final String processType = "ei";
        final String operationType = "createEI";
        final String owner = getOwner(authorization);
        final Params params = new Params(operationId, null, null, "ei",
                processType, operationType, owner, null, null, null, null, null);
        final String requestId = UUIDs.timeBased().toString();
        saveRequest(requestId, operationId, params, jsonData);
        checkOperationById(operationId);
        Map<String, Object> variables = new HashMap<>();
        variables.put("requestId", requestId);
        variables.put("isTokenPresent", 0);
        startProcess(processType, operationId, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/ei/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> updateEIN(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @RequestHeader("X-TOKEN") final String token,
                                            @PathVariable("ocid") final String ocid,
                                            @RequestBody final JsonNode jsonData) {
        final String processType = "ei";
        final String operationType = "updateEI";
        final String owner = getOwner(authorization);
        final Params params = new Params(operationId, ocid, null, "ei", processType, operationType, owner,
                null, null, token, null, null);
        final String requestId = UUIDs.timeBased().toString();
        saveRequest(requestId, operationId, params, jsonData);
        checkOperationById(operationId);
        Map<String, Object> variables = new HashMap<>();
        variables.put("requestId", requestId);
        variables.put("isTokenPresent", 1);
        startProcess(processType, operationId, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/fs", method = RequestMethod.POST)
    public ResponseEntity<String> createFS(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestParam(value = "cpid") final String cpid,
                                           @RequestBody final JsonNode jsonData) {
        final String processType = "fs";
        final String operationType = "createFS";
        final String owner = getOwner(authorization);
        final Params params = new Params(operationId, cpid, null, "fs", processType, operationType, owner,
                null, null, null, null, null);
        final String requestId = UUIDs.timeBased().toString();
        saveRequest(requestId, operationId, params, jsonData);
        checkOperationById(operationId);
        Map<String, Object> variables = new HashMap<>();
        variables.put("requestId", requestId);
        variables.put("isTokenPresent", 0);
        startProcess(processType, operationId, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/fs/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> updateFS(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestHeader("X-TOKEN") final String token,
                                           @PathVariable("ocid") final String ocid,
                                           @RequestParam("cpid") final String cpid,
                                           @RequestBody final JsonNode jsonData) {
        final String processType = "fs";
        final String operationType = "updateFS";
        final String owner = getOwner(authorization);
        final Params params = new Params(operationId, cpid, ocid, "fs", processType, operationType, owner,
                null, null, token, null, null);
        final String requestId = UUIDs.timeBased().toString();
        saveRequest(requestId, operationId, params, jsonData);
        checkOperationById(operationId);
        Map<String, Object> variables = new HashMap<>();
        variables.put("requestId", requestId);
        variables.put("isTokenPresent", 1);
        startProcess(processType, operationId, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }
}

