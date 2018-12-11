package com.procurement.orchestrator.controller;

import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.service.RequestService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocumentUpdateController extends DoBaseController {

    private final ProcessService processService;
    private final RequestService requestService;

    public DocumentUpdateController(final ProcessService processService,
                                    final RequestService requestService) {
        this.processService = processService;
        this.requestService = requestService;
    }




    @RequestMapping(value = "/can-docs/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> updateDocuments(@RequestHeader("Authorization") final String authorization,
                                               @RequestHeader("X-OPERATION-ID") final String operationId,
                                               @RequestHeader("X-TOKEN") final String token,
                                               @PathVariable("cpid") final String cpid,
                                               @PathVariable("ocid") final String ocid) {
        requestService.validate(operationId, null);
        final Context context = requestService.getContextForContractUpdate(authorization, operationId,
                                                                           cpid, ocid, token, "can-docs");
        requestService.saveRequestAndCheckOperation(context, null);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", context.getOperationType());
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }



}

