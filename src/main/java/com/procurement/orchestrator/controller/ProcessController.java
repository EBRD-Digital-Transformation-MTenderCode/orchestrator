package com.procurement.orchestrator.controller;

import com.procurement.orchestrator.domain.constant.ResponseMessageType;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/processes")
public class ProcessController {

    private final ProcessService processService;

    private final OperationService operationService;

    private final JsonUtil jsonUtil;

    public ProcessController(final ProcessService processService,
                             final OperationService operationService,
                             final JsonUtil jsonUtil) {
        this.processService = processService;
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
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
}

