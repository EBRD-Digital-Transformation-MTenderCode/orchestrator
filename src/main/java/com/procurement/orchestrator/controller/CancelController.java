package com.procurement.orchestrator.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
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
                                               @RequestHeader("X-OPERATION-ID") final UUID operationId,
                                               @RequestHeader("X-TOKEN") final String token,
                                               @PathVariable("cpid") final String cpid,
                                               @PathVariable("ocid") final String ocid,
                                               @PathVariable("id") final UUID id) {
        final Context context = requestService.getContextForUpdate(authorization, operationId.toString(), cpid, ocid, token, "bidWithdrawn");
        context.setId(id.toString());
        requestService.saveRequestAndCheckOperation(context, jsonUtil.empty());
        final Map<String, Object> variables = new HashMap<>();
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/cn/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> tenderCancellation(@RequestHeader("Authorization") final String authorization,
                                                     @RequestHeader("X-OPERATION-ID") final UUID operationId,
                                                     @RequestHeader("X-TOKEN") final String token,
                                                     @PathVariable("cpid") final String cpid,
                                                     @PathVariable("ocid") final String ocid,
                                                     @RequestBody final JsonNode data) {
        final Context context = requestService.getContextForUpdate(authorization, operationId.toString(), cpid, ocid, token, "tenderCancellation");
        requestService.saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }
}

