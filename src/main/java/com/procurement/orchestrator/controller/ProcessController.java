package com.procurement.orchestrator.controller;

import com.procurement.orchestrator.cassandra.OperationService;
import com.procurement.orchestrator.domain.constant.ResponseMessageType;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/processes")
public class ProcessController {

    private final ProcessService processService;

    private final OperationService operationService;

    private final JsonUtil jsonUtil;

    int count = 0;

    public ProcessController(final ProcessService processService,
                             final OperationService operationService,
                             final JsonUtil jsonUtil) {
        this.processService = processService;
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
    }

    @RequestMapping(value = "/createCN", method = RequestMethod.POST)
    public ResponseEntity<String> createCN() {
        count = count + 1;
        String transactionId = Integer.toString(count);
        String platformId = "front";
        String processType = "createCN";
        String jsonData = jsonUtil.getResource( "processes/json/cn.json");
        /**check/save operation data*/
        operationService.processFirstOperationStep(transactionId, platformId, processType, jsonData);
        /**start new process for current operation*/
        processService.startProcess(processType, transactionId);
        return new ResponseEntity<>(ResponseMessageType.OK.value(), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/startProcess", method = RequestMethod.POST)
    public ResponseEntity<String> startProcess(@RequestParam final String transactionId,
                                               @RequestParam final String processType,
                                               @RequestParam final String platformId,
                                               @RequestBody final String jsonData) {
        /**check/save operation data*/
        operationService.processFirstOperationStep(transactionId, platformId, processType, jsonData);
        /**start new process for current operation*/
        processService.startProcess(processType, transactionId);
        return new ResponseEntity<>(ResponseMessageType.OK.value(), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/testChronographProcess", method = RequestMethod.POST)
    public ResponseEntity<String> testChronographProcess(@RequestParam final String transactionId) {
        /**start new process for current operation*/
        String processType = "testChronographProducer";
        processService.startProcess(processType, transactionId);
        return new ResponseEntity<>(ResponseMessageType.OK.value(), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/testProcessEin", method = RequestMethod.POST)
    public ResponseEntity<String> testProcessEin() {
        count = count + 1;
        String transactionId = Integer.toString(count);
        String platformId = "front";
        String processType = "ein";
        String jsonData = jsonUtil.getResource( "processes/json/ein.json");
        /**check/save operation data*/
        operationService.processFirstOperationStep(transactionId, platformId, processType, jsonData);
        /**start new process for current operation*/
        processService.startProcess(processType, transactionId);
        return new ResponseEntity<>(ResponseMessageType.OK.value(), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/testProcessFs", method = RequestMethod.POST)
    public ResponseEntity<String> testProcessFs() {
        count = count + 1;
        String transactionId = Integer.toString(count);
        String platformId = "front";
        String processType = "fs";
        String jsonData = jsonUtil.getResource( "processes/json/fs.json");
        /**check/save operation data*/
        operationService.processFirstOperationStep(transactionId, platformId, processType, jsonData);
        /**start new process for current operation*/
        processService.startProcess(processType, transactionId);
        return new ResponseEntity<>(ResponseMessageType.OK.value(), HttpStatus.CREATED);
    }
}

