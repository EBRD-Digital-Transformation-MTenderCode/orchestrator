package com.procurement.orchestrator.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.service.RequestService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/issue")
public class IssueController {

    private final DateUtil dateUtil;
    private final ProcessService processService;
    private final RequestService requestService;
    private final JsonUtil jsonUtil;

    public IssueController(final ProcessService processService,
                           final RequestService requestService,
                           final JsonUtil jsonUtil,
                           final DateUtil dateUtil) {
        this.dateUtil = dateUtil;
        this.jsonUtil = jsonUtil;
        this.processService = processService;
        this.requestService = requestService;
    }

    @RequestMapping(value = "/contract/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> issuingAC(
            @RequestHeader("Authorization") final String authorization,
            @RequestHeader("X-OPERATION-ID") final String operationId,
            @RequestHeader("X-TOKEN") final String token,
            @PathVariable("cpid") final String cpid,
            @PathVariable("ocid") final String ocid,
            @RequestBody(required = false) final JsonNode data
    ) {

        requestService.validate(operationId, data);
        final Context context = requestService.getContextForContractUpdate(authorization, operationId, cpid, ocid, token, "issuingAC");
        requestService.saveRequestAndCheckOperation(context, data);

        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", context.getOperationType());
        processService.startProcess(context, variables);

        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }
}