package com.procurement.orchestrator.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.service.RequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AmendController {
    private final ProcessService processService;
    private final RequestService requestService;

    public AmendController(final ProcessService processService,
                            final RequestService requestService) {
        this.processService = processService;
        this.requestService = requestService;
    }

    @RequestMapping(value = "/amend/fe/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> amendFe(@RequestHeader("Authorization") final String authorization,
                                          @RequestHeader("X-OPERATION-ID") final String operationId,
                                          @RequestHeader("X-TOKEN") final String token,
                                          @PathVariable("cpid") final String cpid,
                                          @PathVariable("ocid") final String ocid,
                                          @RequestBody final JsonNode data) {
        requestService.validate(operationId, data);
        final String processType = "amendFE";
        final Context context =
                requestService.getContextForUpdate(authorization, operationId, cpid, ocid, token, processType);
        processService.setPreQualificationPeriodStartDate(data, context.getStartDate(), null);
        requestService.saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", context.getOperationType());
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }
}
