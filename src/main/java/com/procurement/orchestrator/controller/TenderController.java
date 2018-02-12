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
public class TenderController extends BaseController {

    public TenderController(final ProcessService processService,
                            final RequestService requestService,
                            final OperationService operationService,
                            final JsonUtil jsonUtil) {
        super(jsonUtil, requestService, operationService, processService);
    }

    @RequestMapping(value = "/cn", method = RequestMethod.POST)
    public ResponseEntity<String> createCN(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestParam("country") final String country,
                                           @RequestParam("pmd") final String pmd,
                                           @RequestBody final JsonNode jsonData) {
        final String processType = "createCN";
        final String operationType = "createCN";
        final String owner = getOwner(authorization);
        final Params params = new Params(operationId, null, null, "ps", processType, operationType, owner,
                country, pmd, null, null, null);
        final String requestId = UUIDs.timeBased().toString();
        saveRequest(requestId, operationId, params, jsonData);
        checkOperationById(operationId);
        Map<String, Object> variables = new HashMap<>();
        variables.put("requestId", requestId);
        startProcess(processType, operationId, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }


    @RequestMapping(value = "/bid", method = RequestMethod.POST)
    public ResponseEntity<String> createBid(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @RequestHeader(value = "X-TOKEN", required = false) final String token,
                                            @RequestParam("cpid") final String cpid,
                                            @RequestBody final JsonNode jsonData) {
        final String processType = "submitTheBid";
        final String operationType = "bid";
        final String owner = getOwner(authorization);
        final Params params = new Params(operationId, cpid, null, "ps", processType, operationType, owner,
                null, null, token, null, null);
        final String requestId = UUIDs.timeBased().toString();
        saveRequest(requestId, operationId, params, jsonData);
        checkOperationById(operationId);
        Map<String, Object> variables = new HashMap<>();
        variables.put("requestId", requestId);
        variables.put("isTokenPresent", ((token == null || "".equals(token.trim())) ? 0 : 1));
        startProcess(processType, operationId, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }
}

