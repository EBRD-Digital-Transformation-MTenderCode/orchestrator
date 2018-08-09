package com.procurement.orchestrator.controller;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Country;
import com.procurement.orchestrator.domain.Stage;
import com.procurement.orchestrator.exception.OperationException;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.service.RequestService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BudgetController extends BaseController {

    private final DateUtil dateUtil;
    private final ProcessService processService;
    private final OperationService operationService;
    private final String lang = "ro";

    public BudgetController(final ProcessService processService,
                            final RequestService requestService,
                            final OperationService operationService,
                            final JsonUtil jsonUtil,
                            final DateUtil dateUtil) {
        super(jsonUtil, requestService, operationService);
        this.dateUtil = dateUtil;
        this.processService = processService;
        this.operationService = operationService;
    }

    @RequestMapping(value = "/ei", method = RequestMethod.POST)
    public ResponseEntity<String> createEI(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final UUID operationId,
                                           @RequestParam("country") final String country,
                                           @RequestBody final JsonNode data) {
        final Context context = new Context();
        context.setRequestId(UUIDs.timeBased().toString());
        context.setOperationId(operationId.toString());
        context.setCountry(Country.fromValue(country.toUpperCase()).value());
        context.setLanguage(lang);
        context.setStartDate(dateUtil.nowFormatted());
        context.setStage(Stage.EI.value());
        context.setOwner(getOwner(authorization));
        context.setProcessType("ei");
        context.setOperationType("createEI");
        saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("isTokenPresent", 0);
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/ei/{cpid}", method = RequestMethod.POST)
    public ResponseEntity<String> updateEI(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final UUID operationId,
                                           @RequestHeader("X-TOKEN") final String token,
                                           @PathVariable("cpid") final String cpid,
                                           @RequestBody final JsonNode data) {
        final Context context = new Context();
        final Context prevContext = operationService.getContext(cpid);
        context.setCountry(prevContext.getCountry());
        context.setLanguage(lang);

        context.setRequestId(UUIDs.timeBased().toString());
        context.setOperationId(operationId.toString());
        context.setCpid(cpid);
        context.setStartDate(dateUtil.nowFormatted());
        context.setStage(Stage.EI.value());
        context.setProcessType("ei");
        context.setOperationType("updateEI");
        context.setOwner(getOwner(authorization));
        context.setToken(token);
        saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("isTokenPresent", (context.getToken() == null || "".equals(context.getToken().trim())) ? 0 : 1);
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/fs/{cpid}", method = RequestMethod.POST)
    public ResponseEntity<String> createFS(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final UUID operationId,
                                           @PathVariable("cpid") final String cpid,
                                           @RequestBody final JsonNode data) {
        final Context context = new Context();
        final Context prevContext = operationService.getContext(cpid);
        context.setCountry(prevContext.getCountry());
        context.setLanguage(lang);

        context.setRequestId(UUIDs.timeBased().toString());
        context.setOperationId(operationId.toString());
        context.setStartDate(dateUtil.nowFormatted());
        context.setCpid(cpid);
        context.setStage(Stage.FS.value());
        context.setProcessType("fs");
        context.setOperationType("createFS");
        context.setOwner(getOwner(authorization));
        saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("isTokenPresent", 0);
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/fs/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> updateFS(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final UUID operationId,
                                           @RequestHeader("X-TOKEN") final String token,
                                           @PathVariable("cpid") final String cpid,
                                           @PathVariable("ocid") final String ocid,
                                           @RequestBody final JsonNode data) {
        validateOcId(cpid, ocid);
        final Context context = new Context();
        final Context prevContext = operationService.getContext(cpid);
        context.setCountry(prevContext.getCountry());
        context.setLanguage(lang);

        context.setRequestId(UUIDs.timeBased().toString());
        context.setOperationId(operationId.toString());
        context.setCpid(cpid);
        context.setOcid(ocid);
        context.setStartDate(dateUtil.nowFormatted());
        context.setStage(Stage.FS.value());
        context.setProcessType("fs");
        context.setOperationType("updateFS");
        context.setOwner(getOwner(authorization));
        context.setToken(token);
        saveRequestAndCheckOperation(context, data);
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

