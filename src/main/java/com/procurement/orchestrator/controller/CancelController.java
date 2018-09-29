package com.procurement.orchestrator.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.exception.OperationException;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.service.RequestService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/cancel")
public class CancelController {

    private final ProcessService processService;
    private final RequestService requestService;
    private final JsonUtil jsonUtil;

    public CancelController(final ProcessService processService,
                            final RequestService requestService,
                            final JsonUtil jsonUtil) {
        this.processService = processService;
        this.requestService = requestService;
        this.jsonUtil = jsonUtil;
    }


    @RequestMapping(value = "/bid/{cpid}/{ocid}/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> bidWithdrawn(@RequestHeader("Authorization") final String authorization,
                                               @RequestHeader("X-OPERATION-ID") final String operationId,
                                               @RequestHeader("X-TOKEN") final String token,
                                               @PathVariable("cpid") final String cpid,
                                               @PathVariable("ocid") final String ocid,
                                               @PathVariable("id") final String id) {

        requestService.validate(operationId, null);
        final Context context = requestService.getContextForUpdate(authorization, operationId, cpid, ocid, token, "bidWithdrawn");
        context.setId(id);
        requestService.saveRequestAndCheckOperation(context, jsonUtil.empty());
        final Map<String, Object> variables = new HashMap<>();
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/cn/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> cnCancellation(@RequestHeader("Authorization") final String authorization,
                                                 @RequestHeader("X-OPERATION-ID") final String operationId,
                                                 @RequestHeader("X-TOKEN") final String token,
                                                 @PathVariable("cpid") final String cpid,
                                                 @PathVariable("ocid") final String ocid,
                                                 @RequestBody final JsonNode data) {
        requestService.validate(operationId, data);
        final Context context = requestService.getContextForUpdate(authorization, operationId, cpid, ocid, token, "cnCancellation");
        if (ocid.contains("PN") || ocid.contains("PIN")) throw new OperationException("Invalid ocid.");
        requestService.saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", context.getOperationType());
        variables.put("phase", context.getPhase());
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/pin/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> pinCancellation(@RequestHeader("Authorization") final String authorization,
                                                  @RequestHeader("X-OPERATION-ID") final String operationId,
                                                  @RequestHeader("X-TOKEN") final String token,
                                                  @PathVariable("cpid") final String cpid,
                                                  @PathVariable("ocid") final String ocid) {
        requestService.validate(operationId, null);
        final Context pinContext = requestService.getContextForUpdate(authorization, operationId, cpid, ocid, token, "pinCancellation");
        if (!ocid.contains("PIN")) throw new OperationException("Invalid ocid.");
        requestService.saveRequestAndCheckOperation(pinContext, jsonUtil.empty());
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", pinContext.getOperationType());
        processService.startProcess(pinContext, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/pn/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> pnCancellation(@RequestHeader("Authorization") final String authorization,
                                                 @RequestHeader("X-OPERATION-ID") final String operationId,
                                                 @RequestHeader("X-TOKEN") final String token,
                                                 @PathVariable("cpid") final String cpid,
                                                 @PathVariable("ocid") final String ocid) {
        requestService.validate(operationId, null);
        final Context pnContext = requestService.getContextForUpdate(authorization, operationId, cpid, ocid, token, "pnCancellation");
        if (!ocid.contains("PN")) throw new OperationException("Invalid ocid.");
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", pnContext.getOperationType());
        requestService.saveRequestAndCheckOperation(pnContext, jsonUtil.empty());
        processService.startProcess(pnContext, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

}

