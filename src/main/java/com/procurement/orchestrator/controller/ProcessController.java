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

    int count=0;


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

    @RequestMapping(value = "/testProcess", method = RequestMethod.POST)
    public ResponseEntity<String> testProcess() {
        count=count+1;
        String transactionId = Integer.toString(count);
        String platformId = "front";
        String processType = "ein";
        String jsonData = "{\"tender\":{\"id\":\"string\",\"title\":\"string\",\"description\":\"string\"," +
            "\"classification\":{\"id\":\"string\",\"scheme\":\"CPV\",\"description\":\"string\"}}," +
            "\"planning\":{\"budget\":{\"id\":\"string\"," +
            "\"period\":{\"startDate\":\"2017-12-05T16:04:25.000000107Z\"," +
            "\"endDate\":\"2017-12-05T16:04:25.000000107Z\"},\"amount\":{\"amount\":1.9,\"currency\":\"AED\"}}," +
            "\"rationale\":\"string\"},\"relatedProcesses\":[{\"id\":\"string\",\"relationship\":[\"framework\"]," +
            "\"title\":\"string\",\"scheme\":\"ocid\",\"identifier\":\"string\",\"uri\":\"string\"}]," +
            "\"parties\":[{\"id\":\"string\",\"name\":\"string\",\"identifier\":{\"id\":\"string\"," +
            "\"scheme\":\"string\",\"legalName\":\"string\",\"uri\":\"string\"}," +
            "\"additionalIdentifiers\":[{\"id\":\"string\",\"scheme\":\"string\",\"legalName\":\"string\"," +
            "\"uri\":\"string\"}],\"address\":{\"streetAddress\":\"string\",\"locality\":\"string\"," +
            "\"region\":\"string\",\"postalCode\":\"string\",\"countryName\":\"string\"}," +
            "\"contactPoint\":{\"name\":\"string\",\"email\":\"string\",\"telephone\":\"string\"," +
            "\"faxNumber\":\"string\",\"url\":\"string\"},\"roles\":[\"buyer\"]," +
            "\"details\":{\"typeOfBuyer\":\"BODY_PUBLIC\",\"mainGeneralActivity\":\"DEFENCE\"," +
            "\"mainSectoralActivity\":\"AIRPORT_RELATED_ACTIVITIES\",\"isACentralPurchasingBody\":true," +
            "\"NUTSCode\":\"string\",\"scale\":\"micro\"},\"buyerProfile\":\"string\"}],\"language\":\"string\"," +
            "\"buyer\":{\"id\":\"string\",\"name\":\"string\",\"identifier\":{\"id\":\"string\"," +
            "\"scheme\":\"string\",\"legalName\":\"string\",\"uri\":\"string\"}," +
            "\"address\":{\"streetAddress\":\"string\",\"locality\":\"string\",\"region\":\"string\"," +
            "\"postalCode\":\"string\",\"countryName\":\"string\"},\"additionalIdentifiers\":[{\"id\":\"string\"," +
            "\"scheme\":\"string\",\"legalName\":\"string\",\"uri\":\"string\"}]," +
            "\"contactPoint\":{\"name\":\"string\",\"email\":\"string\",\"telephone\":\"string\"," +
            "\"faxNumber\":\"string\",\"url\":\"string\"}}}";

        /**check/save operation data*/
        operationService.processFirstOperationStep(transactionId, platformId, processType, jsonData);
        /**start new process for current operation*/
        processService.startProcess(processType, transactionId);
        return new ResponseEntity<>(ResponseMessageType.OK.value(), HttpStatus.CREATED);
    }
}

