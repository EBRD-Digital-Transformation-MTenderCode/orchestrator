package com.procurement.orchestrator.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.service.RequestService;
import com.procurement.orchestrator.utils.DateUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cancel")
public class CancelController {

    private final ProcessService processService;
    private final RequestService requestService;

    public CancelController(final ProcessService processService,
                            final RequestService requestService,
                            final DateUtil dateUtil) {
        this.processService = processService;
        this.requestService = requestService;
    }


    @RequestMapping(value = "/bid/{cpid}/{ocid}/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> bidWithdrawn(@RequestHeader("Authorization") final String authorization,
                                               @RequestHeader("X-OPERATION-ID") final UUID operationId,
                                               @RequestHeader("X-TOKEN") final String token,
                                               @PathVariable("cpid") final String cpid,
                                               @PathVariable("ocid") final String ocid,
                                               @PathVariable("id") final UUID id,
                                               @RequestBody final JsonNode data) {
        final Context context = requestService.getContextForUpdate(authorization, operationId.toString(), cpid, ocid, token, "bidWithdrawn");
        context.setId(id.toString());
        requestService.saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }
}

