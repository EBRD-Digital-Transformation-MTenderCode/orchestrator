package com.procurement.orchestrator.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.cassandra.service.RequestService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
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

    @RequestMapping(value = "{processType}", method = RequestMethod.POST)
    public ResponseEntity<String> doCreate(@PathVariable("processType") final String processType,
                                           @RequestHeader("txId") final String txId,
                                           @RequestHeader("token") final String token,
                                           @RequestHeader("Authorization") final String authorization,
                                           @RequestParam("country") final String country,
                                           @RequestParam("pmd") final String pmd,
                                           @RequestBody final JsonNode jsonData) {
        final String jsonDataString = jsonUtil.getResource("processes/json/cn.json");
        final JsonNode jsonDataFromFile = jsonUtil.toJsonNode(jsonDataString);
        final Params params = new Params(txId, null, token, processType, authorization, country, pmd);
        requestService.saveRequest(txId, params, jsonDataFromFile);
        operationService.checkOperationByTxId(txId);
        processService.startProcess(processType, txId);
        return new ResponseEntity<>("ok", HttpStatus.CREATED);
    }

    @RequestMapping(value = "{processType}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> doCreate(@PathVariable("processType") final String processType,
                                           @PathVariable("ocid") final String ocid,
                                           @RequestHeader("txId") final String txId,
                                           @RequestHeader("token")final  String token,
                                           @RequestHeader("Authorization") final String authorization,
                                           @RequestParam("country")final  String country,
                                           @RequestParam("pmd") final String pmd,
                                           @RequestBody final JsonNode jsonData) {
        final String jsonDataString = jsonUtil.getResource("processes/json/cn.json");
        final JsonNode jsonDataFromFile = jsonUtil.toJsonNode(jsonDataString);
        final Params params = new Params(txId, ocid, token, processType, authorization, country, pmd);
        requestService.saveRequest(txId, params, jsonDataFromFile);
        operationService.checkOperationByTxId(txId);
        processService.startProcess(processType, txId);
        return new ResponseEntity<>("ok", HttpStatus.CREATED);
    }

//    @RequestMapping(value = "/testProcess/{cpid}", method = RequestMethod.POST)
//    public ResponseEntity<String> startProcess(@PathParam("cpid") String cpid) {
//        /**check/save operation data*/
//        count = count + 1;
//        String jsonData = "{\"m\":\"n\"}";
//        /**check/save operation data*/
//        operationService.processFirstOperationStep(Integer.toString(count), "234", "test", jsonData);
//        return new ResponseEntity<>(ResponseMessageType.OK.value(), HttpStatus.CREATED);
//    }
//
//    @RequestMapping(value = "/testProcess", method = RequestMethod.POST)
//    public ResponseEntity<String> testProcess() {
//        /**check/save operation data*/
//        count = count + 1;
//        String jsonData = "{\"m\":\"n\"}";
//        /**check/save operation data*/
//        operationService.processFirstOperationStep(Integer.toString(count), "234", "test", jsonData);
//        return new ResponseEntity<>(ResponseMessageType.OK.value(), HttpStatus.CREATED);
//    }
//
//    @RequestMapping(value = "/createCN", method = RequestMethod.POST)
//    public ResponseEntity<String> createCN() {
//        count = count + 1;
//        String transactionId = Integer.toString(count);
//        String platformId = "front";
//        String processType = "createCN";
//        String jsonData = jsonUtil.getResource("processes/json/cn.json");
//        /**check/save operation data*/
//        operationService.processFirstOperationStep(transactionId, platformId, processType, jsonData);
//        /**start new process for current operation*/
//        processService.startProcess(processType, transactionId);
//        return new ResponseEntity<>(ResponseMessageType.OK.value(), HttpStatus.CREATED);
//    }
//
//    @RequestMapping(value = "/startProcess", method = RequestMethod.POST)
//    public ResponseEntity<String> startProcess(@RequestParam final String transactionId,
//                                               @RequestParam final String processType,
//                                               @RequestParam final String platformId,
//                                               @RequestBody final String jsonData) {
//        /**check/save operation data*/
//        operationService.processFirstOperationStep(transactionId, platformId, processType, jsonData);
//        /**start new process for current operation*/
//        processService.startProcess(processType, transactionId);
//        return new ResponseEntity<>(ResponseMessageType.OK.value(), HttpStatus.CREATED);
//    }
//
//    @RequestMapping(value = "/testChronographProcess", method = RequestMethod.POST)
//    public ResponseEntity<String> testChronographProcess(@RequestParam final String transactionId) {
//        /**start new process for current operation*/
//        String processType = "testChronographProducer";
//        processService.startProcess(processType, transactionId);
//        return new ResponseEntity<>(ResponseMessageType.OK.value(), HttpStatus.CREATED);
//    }
//
//    @RequestMapping(value = "/testProcessEin", method = RequestMethod.POST)
//    public ResponseEntity<String> testProcessEin() {
//        count = count + 1;
//        String transactionId = Integer.toString(count);
//        String platformId = "front";
//        String processType = "ein";
//        String jsonData = jsonUtil.getResource("processes/json/ein.json");
//        /**check/save operation data*/
//        operationService.processFirstOperationStep(transactionId, platformId, processType, jsonData);
//        /**start new process for current operation*/
//        processService.startProcess(processType, transactionId);
//        return new ResponseEntity<>(ResponseMessageType.OK.value(), HttpStatus.CREATED);
//    }
//
//    @RequestMapping(value = "/testProcessFs", method = RequestMethod.POST)
//    public ResponseEntity<String> testProcessFs() {
//        count = count + 1;
//        String transactionId = Integer.toString(count);
//        String platformId = "front";
//        String processType = "fs";
//        String jsonData = jsonUtil.getResource("processes/json/fs.json");
//        /**check/save operation data*/
//        operationService.processFirstOperationStep(transactionId, platformId, processType, jsonData);
//        /**start new process for current operation*/
//        processService.startProcess(processType, transactionId);
//        return new ResponseEntity<>(ResponseMessageType.OK.value(), HttpStatus.CREATED);
//    }

    private JsonNode getJsonParams(final String txId,
                                   final String ocid,
                                   final String token,
                                   final String processType,
                                   final String authorization,
                                   final String country,
                                   final String pmd) {
        final ObjectNode jsonParams = jsonUtil.createObjectNode();
        jsonParams.put("txId", txId);
        jsonParams.put("ocid", ocid);
        jsonParams.put("token", token);
        jsonParams.put("processType", processType);
        jsonParams.put("authorization", authorization);
        jsonParams.put("country", country);
        jsonParams.put("pmd", pmd);
        return jsonParams;
    }

}

