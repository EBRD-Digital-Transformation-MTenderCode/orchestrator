package com.procurement.orchestrator.controller;

import com.procurement.orchestrator.cassandra.OperationService;
import com.procurement.orchestrator.domain.constant.ResponseMessageType;
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

    int count = 0;

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

    @RequestMapping(value = "/testProcessEin", method = RequestMethod.POST)
    public ResponseEntity<String> testProcessEin() {
        count = count + 1;
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

    @RequestMapping(value = "/testProcessFs", method = RequestMethod.POST)
    public ResponseEntity<String> testProcessFs() {
        count = count + 1;
        String transactionId = Integer.toString(count);
        String platformId = "front";
        String processType = "fs";
        String jsonData = "{\n" +
            "  \"planning\": {\n" +
            "    \"budget\": {\n" +
            "      \"id\": \"string\",\n" +
            "      \"description\": \"string\",\n" +
            "      \"amount\": {\n" +
            "        \"amount\": 100.20,\n" +
            "        \"currency\": \"AED\"\n" +
            "      },\n" +
            "      \"project\": \"string\",\n" +
            "      \"projectID\": \"string\",\n" +
            "      \"uri\": \"string\",\n" +
            "      \"isEuropeanUnionFunded\": true,\n" +
            "      \"sourceEntity\": {\n" +
            "        \"name\": \"string\",\n" +
            "        \"id\": \"string\"\n" +
            "      },\n" +
            "      \"period\": {\n" +
            "        \"startDate\": \"2017-12-13T16:21:54.642Z\",\n" +
            "        \"endDate\": \"2017-12-13T16:21:54.642Z\"\n" +
            "      },\n" +
            "      \"verified\": true,\n" +
            "      \"verificationDetails\": \"string\"\n" +
            "    },\n" +
            "    \"rationale\": \"string\"\n" +
            "  },\n" +
            "  \"parties\": {\n" +
            "    \"id\": \"string\",\n" +
            "    \"name\": \"string\",\n" +
            "    \"identifier\": {\n" +
            "      \"scheme\": \"string\",\n" +
            "      \"id\": \"string\",\n" +
            "      \"legalName\": \"string\",\n" +
            "      \"uri\": \"string\"\n" +
            "    },\n" +
            "    \"additionalIdentifiers\": [\n" +
            "      {\n" +
            "        \"scheme\": \"string\",\n" +
            "        \"id\": \"string\",\n" +
            "        \"legalName\": \"string\",\n" +
            "        \"uri\": \"string\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"address\": {\n" +
            "      \"streetAddress\": \"string\",\n" +
            "      \"locality\": \"string\",\n" +
            "      \"region\": \"string\",\n" +
            "      \"postalCode\": \"string\",\n" +
            "      \"countryName\": \"string\"\n" +
            "    },\n" +
            "    \"contactPoint\": {\n" +
            "      \"name\": \"string\",\n" +
            "      \"email\": \"string\",\n" +
            "      \"telephone\": \"string\",\n" +
            "      \"faxNumber\": \"string\",\n" +
            "      \"url\": \"string\"\n" +
            "    },\n" +
            "    \"roles\": \"buyer\",\n" +
            "    \"details\": {\n" +
            "      \"typeOfBuyer\": \"BODY_PUBLIC\",\n" +
            "      \"mainGeneralActivity\": \"DEFENCE\",\n" +
            "      \"mainSectoralActivity\": \"AIRPORT_RELATED_ACTIVITIES\",\n" +
            "      \"isACentralPurchasingBody\": true,\n" +
            "      \"NUTSCode\": \"string\",\n" +
            "      \"scale\": \"micro\"\n" +
            "    },\n" +
            "    \"buyerProfile\": \"string\"\n" +
            "  },\n" +
            "  \"relatedProcesses\": [\n" +
            "    {\n" +
            "      \"id\": \"string\",\n" +
            "      \"relationship\": \"framework\",\n" +
            "      \"title\": \"string\",\n" +
            "      \"scheme\": \"ocid\",\n" +
            "      \"identifier\": \"ocds-t1s2t3-UA-1513433769869\",\n" +
            "      \"uri\": \"string\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

        /**check/save operation data*/
        operationService.processFirstOperationStep(transactionId, platformId, processType, jsonData);
        /**start new process for current operation*/
        processService.startProcess(processType, transactionId);
        return new ResponseEntity<>(ResponseMessageType.OK.value(), HttpStatus.CREATED);
    }
}

