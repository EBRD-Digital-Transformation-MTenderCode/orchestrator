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
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/do")
public class ProcessController {

    private final ProcessService processService;

    private final RequestService requestService;

    private final OperationService operationService;

    private final JsonUtil jsonUtil;


    public ProcessController(final ProcessService processService,
                             final RequestService requestService,
                             final OperationService operationService,
                             final JsonUtil jsonUtil) {
        this.processService = processService;
        this.requestService = requestService;
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
    }

    @RequestMapping(value = "/ei", method = RequestMethod.POST)
    public ResponseEntity<String> createEIN(@RequestHeader("X-OPERATION-ID") final String operationId,
                                            @RequestHeader("Authorization") final String authorization,
                                            @RequestBody final JsonNode jsonData) {
        final String processType = "ei";
        final String operationType = "createEI";
        final String owner = getOwner(authorization);
        final Params params = new Params(operationId, null, null, "ei",
                processType, operationType, owner, null, null, null, null, null);
        final String requestId = UUIDs.timeBased().toString();
        requestService.saveRequest(requestId, operationId, params, jsonData);
        operationService.checkOperationById(operationId);
        Map<String, Object> variables = new HashMap<>();
        variables.put("requestId", requestId);
        variables.put("isTokenPresent", 0);
        processService.startProcess(processType, operationId, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/ei/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> updateEIN(@PathVariable("ocid") final String ocid,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("token") final String token,
                                            @RequestBody final JsonNode jsonData) {
        final String processType = "ei";
        final String operationType = "updateEI";
        final String owner = getOwner(authorization);
        final Params params = new Params(operationId, ocid, null, "ei", processType, operationType, owner,
                null, null, token, null, null);
        final String requestId = UUIDs.timeBased().toString();
        requestService.saveRequest(requestId, operationId, params, jsonData);
        operationService.checkOperationById(operationId);
        Map<String, Object> variables = new HashMap<>();
        variables.put("requestId", requestId);
        variables.put("isTokenPresent", 1);
        processService.startProcess(processType, operationId, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/fs", method = RequestMethod.POST)
    public ResponseEntity<String> createFS(@RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestHeader("Authorization") final String authorization,
                                           @RequestParam(value = "cpid", required = false) final String cpid,
                                           @RequestBody final JsonNode jsonData) {
        final String processType = "fs";
        final String operationType = "createFS";
        final String owner = getOwner(authorization);
        final Params params = new Params(operationId, cpid, null, "fs", processType, operationType, owner,
                null, null, null, null, null);
        final String requestId = UUIDs.timeBased().toString();
        requestService.saveRequest(requestId, operationId, params, jsonData);
        operationService.checkOperationById(operationId);
        Map<String, Object> variables = new HashMap<>();
        variables.put("requestId", requestId);
        variables.put("isTokenPresent", 0);
        processService.startProcess(processType, operationId, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/fs/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> updateFS(@PathVariable("ocid") final String ocid,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestHeader("Authorization") final String authorization,
                                           @RequestParam("cpid") final String cpid,
                                           @RequestHeader("token") final String token,
                                           @RequestBody final JsonNode jsonData) {
        final String processType = "fs";
        final String operationType = "updateFS";
        final String owner = getOwner(authorization);
        final Params params = new Params(operationId, cpid, ocid, "fs", processType, operationType, owner,
                null, null, token, null, null);
        final String requestId = UUIDs.timeBased().toString();
        requestService.saveRequest(requestId, operationId, params, jsonData);
        operationService.checkOperationById(operationId);
        Map<String, Object> variables = new HashMap<>();
        variables.put("requestId", requestId);
        variables.put("isTokenPresent", 1);
        processService.startProcess(processType, operationId, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }


//    @RequestMapping(value = "{processType}", method = RequestMethod.POST)
//    public ResponseEntity<String> doCreate(@PathVariable("processType") final String processType,
//                                           @RequestHeader("X-OPERATION-ID") final String operationId,
//                                           @RequestHeader("Authorization") final String authorization,
//                                           @RequestHeader(value = "token", required = false) final String token,
//                                           @RequestParam(value = "cpid", required = false) final String cpid,
//                                           @RequestParam(value = "ocid", required = false) final String ocid,
//                                           @RequestParam(value = "stage", required = false) final String stage,
//                                           @RequestParam("country") final String country,
//                                           @RequestParam("pmd") final String pmd,
//                                           @RequestBody final JsonNode jsonData) {
//        final Params params = new Params(
//                operationId,
//                cpid,
//                ocid,
//                stage,
//                processType,
//                "dzo",
//                country,
//                pmd,
//                token,
//                "", "");
//        final String requestId = UUIDs.timeBased().toString();
//        requestService.saveRequest(requestId, operationId, params, jsonData);
//        operationService.checkOperationById(operationId);
//        Map<String, Object> variables = new HashMap<>();
//        variables.put("requestId", requestId);
//        variables.put("isTokenPresent", ((token == null || "".equals(token.trim())) ? 0 : 1));
//        variables.put("checkEnquiries", 0);
//        variables.put("allAnswered", 0);
//        processService.startProcess(processType, operationId, variables);
//        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
//    }


    private String getOwner(String authorization) {
        final String[] split = authorization.split("\\.");
        final String payload = split[1];
        final String encodedToken = StringUtils.newStringUtf8(Base64.decodeBase64(payload.getBytes()));
        JsonNode jsonNode = jsonUtil.toJsonNode(encodedToken);
        return jsonNode.get("idPlatform").asText();
    }

}

