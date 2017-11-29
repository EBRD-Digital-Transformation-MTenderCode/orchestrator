package com.procurement.orchestrator.controller;

import com.procurement.orchestrator.model.dto.ResponseMessage;
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
    public ResponseEntity<String> startProcess(@RequestParam final String operationType,
                                               @RequestParam final String operationId,
                                               @RequestBody final String jsonData) {
        /**check/save operation data*/
        operationService.processOperation(operationType, operationId, jsonData);
        /**start new process for current operation*/
        processService.startProcessInstanceByKey(operationType, operationId);
        return new ResponseEntity<>(ResponseMessage.OK.value(), HttpStatus.CREATED);
    }
}

