package com.procurement.orchestrator.controller;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Country;
import com.procurement.orchestrator.domain.Stage;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.RequestService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.service.ProcessService;
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
        final Params params = new Params();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        params.setStage(Stage.EI.value());
        params.setCountry(Country.fromValue(country).value());
        params.setProcessType("ei");
        params.setOperationType("createEI");
        saveRequestAndCheckOperation(params, jsonData);
        Map<String, Object> variables = new HashMap<>();
        variables.put("isTokenPresent", 0);
        processService.startProcess(params, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/ei/{cpid}", method = RequestMethod.POST)
    public ResponseEntity<String> updateEIN(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @RequestHeader("X-TOKEN") final String token,
                                            @PathVariable("cpid") final String cpid,
                                            @RequestBody final JsonNode jsonData) {
        final Params params = new Params();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOperationId(operationId);
        params.setCpid(cpid);
        params.setStage(Stage.EI.value());
        params.setProcessType("ei");
        params.setOperationType("updateEI");
        params.setOwner(getOwner(authorization));
        params.setToken(token);
        saveRequestAndCheckOperation(params, jsonData);
        Map<String, Object> variables = new HashMap<>();
        variables.put("isTokenPresent", ((params.getToken() == null || "".equals(params.getToken().trim())) ? 0 : 1));
        processService.startProcess(params, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/fs/{cpid}", method = RequestMethod.POST)
    public ResponseEntity<String> createFS(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @PathVariable(value = "cpid") final String cpid,
                                           @RequestBody final JsonNode jsonData) {
        final Params params = new Params();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOperationId(operationId);
        params.setStartDate(dateUtil.format(dateUtil.localDateTimeNowUTC()));
        params.setCpid(cpid);
        params.setStage(Stage.FS.value());
        params.setOperationType("createFS");
        params.setProcessType("fs");
        params.setOwner(getOwner(authorization));
        saveRequestAndCheckOperation(params, jsonData);
        Map<String, Object> variables = new HashMap<>();
        variables.put("isTokenPresent", 0);
        processService.startProcess(params, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/fs/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> updateFS(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestHeader("X-TOKEN") final String token,
                                           @PathVariable("cpid") final String cpid,
                                           @PathVariable("ocid") final String ocid,
                                           @RequestBody final JsonNode jsonData) {
        final Params params = new Params();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOperationId(operationId);
        params.setCpid(cpid);
        params.setOcid(ocid);
        params.setStage(Stage.FS.value());
        params.setProcessType("fs");
        params.setOperationType("updateFS");
        params.setOwner(getOwner(authorization));
        params.setToken(token);
        saveRequestAndCheckOperation(params, jsonData);
        Map<String, Object> variables = new HashMap<>();
        variables.put("isTokenPresent", ((params.getToken() == null || "".equals(params.getToken().trim())) ? 0 : 1));
        processService.startProcess(params, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }
}

