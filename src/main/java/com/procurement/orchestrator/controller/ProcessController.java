package com.procurement.orchestrator.controller;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.cassandra.service.RequestService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.service.ProcessService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/do")
public class ProcessController {

    private final ProcessService processService;

    private final RequestService requestService;

    private final OperationService operationService;


    public ProcessController(final ProcessService processService,
                             final RequestService requestService,
                             final OperationService operationService) {
        this.processService = processService;
        this.requestService = requestService;
        this.operationService = operationService;
    }


    @RequestMapping(value = "{processType}", method = RequestMethod.POST)
    public ResponseEntity<String> doCreate(@PathVariable("processType") final String processType,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestHeader("Authorization") final String authorization,
                                           @RequestHeader(value = "token", required = false) final String token,
                                           @RequestParam(value = "cpid", required = false) final String cpid,
                                           @RequestParam(value = "ocid", required = false) final String ocid,
                                           @RequestParam("country") final String country,
                                           @RequestParam("pmd") final String pmd,
                                           @RequestBody final JsonNode jsonData) {
        final Params params = new Params(operationId, cpid, ocid, processType, "dzo", country, pmd, token);
        final String requestId = UUIDs.timeBased().toString();
        requestService.saveRequest(requestId, operationId, params, jsonData);
        operationService.checkOperationById(operationId);
        Map<String, Object> variables = new HashMap<>();
        variables.put("requestId", requestId);
        variables.put("isTokenPresent", ((token == null || "".equals(token.trim())) ? 0 : 1));
        processService.startProcess(processType, operationId, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

//    @RequestMapping(value = "{processType}/{ocid}", method = RequestMethod.POST)
//    public ResponseEntity<String> doCreate(@PathVariable("processType") final String processType,
//                                           @PathVariable("ocid") final String ocid,
//                                           @RequestHeader("X-OPERATION-ID") final String txId,
//                                           @RequestHeader("token") final String token,
//                                           @RequestHeader("Authorization") final String authorization,
//                                           @RequestParam("country") final String country,
//                                           @RequestParam("pmd") final String pmd,
//                                           @RequestBody final JsonNode jsonData) {
//        final Params params = new Params(txId, ocid, processType, "dzo", country, pmd, token);
//        requestService.saveRequest(txId, params, jsonData);
//        operationService.checkOperationByTxId(txId);
//        processService.startProcess(processType, txId);
//        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
//    }

}

