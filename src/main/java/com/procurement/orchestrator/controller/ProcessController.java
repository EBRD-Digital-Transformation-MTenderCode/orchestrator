package com.procurement.orchestrator.controller;

import com.procurement.orchestrator.domain.constant.ResponseMessageType;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/processes")
public class ProcessController {

    private final ProcessService processService;

    private final OperationService operationService;

    public ProcessController(final ProcessService processService,
                             final OperationService operationService) {
        this.processService = processService;
        this.operationService = operationService;
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

