package com.procurement.orchestrator.controller;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.Params;
import com.procurement.orchestrator.cassandra.model.Stage;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.cassandra.service.RequestService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TenderController extends BaseController {

    public TenderController(final ProcessService processService,
                            final RequestService requestService,
                            final OperationService operationService,
                            final JsonUtil jsonUtil) {
        super(jsonUtil, requestService, operationService, processService);
    }

    @RequestMapping(value = "/cn", method = RequestMethod.POST)
    public ResponseEntity<String> createCN(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestParam("country") final String country,
                                           @RequestParam("pmd") final String pmd,
                                           @RequestBody final JsonNode jsonData) {
        final Params params = new Params();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setStage(Stage.PS.value());
        params.setProcessType("createCN");
        params.setOperationType("createCN");
        params.setCountry(country);
        params.setPmd(pmd);
        return startProcessResult(params, jsonData);
    }

    @RequestMapping(value = "/cn/{cpid}", method = RequestMethod.POST)
    public ResponseEntity<String> updateCN(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @PathVariable("cpid") final String cpid,
                                           @RequestParam("country") final String country,
                                           @RequestParam("pmd") final String pmd,
                                           @RequestBody final JsonNode jsonData) {
        final Params params = new Params();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setCpid(cpid);
        params.setProcessType("updateCN");
        params.setOperationType("updateCN");
        params.setCountry(country);
        params.setPmd(pmd);
        return startProcessResult(params, jsonData);
    }


    @RequestMapping(value = "/bid/{cpid}", method = RequestMethod.POST)
    public ResponseEntity<String> createBid(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @RequestHeader(value = "X-TOKEN", required = false) final String token,
                                            @PathVariable("cpid") final String cpid,
                                            @RequestParam("stage") final String stage,
                                            @RequestBody final JsonNode jsonData) {
        final Params params = new Params();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOperationId(operationId);
        params.setCpid(cpid);
        params.setStage(Stage.fromValue(stage).value());
        params.setProcessType("submitTheBid");
        params.setOperationType("bid");
        params.setOwner(getOwner(authorization));
        params.setToken(token);
        return startProcessResult(params, jsonData);
    }

    @RequestMapping(value = "/enquiry/{cpid}", method = RequestMethod.POST)
    public ResponseEntity<String> enquiry(@RequestHeader("Authorization") final String authorization,
                                          @RequestHeader("X-OPERATION-ID") final String operationId,
                                          @RequestHeader(value = "X-TOKEN", required = false) final String token,
                                          @PathVariable("cpid") final String cpid,
                                          @RequestParam("country") final String country,
                                          @RequestParam("pmd") final String pmd,
                                          @RequestParam("stage") final String stage,
                                          @RequestBody final JsonNode jsonData) {
        final Params params = new Params();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setCpid(cpid);
        params.setStage(Stage.fromValue(stage).value());
        params.setProcessType("enquiry");
        params.setOperationType("createEnquiry");
        params.setCountry(country);
        params.setPmd(pmd);
        params.setToken(token);
        return startProcessResult(params, jsonData);
    }

    @RequestMapping(value = "/award/{cpid}", method = RequestMethod.POST)
    public ResponseEntity<String> awardByBid(@RequestHeader("Authorization") final String authorization,
                                             @RequestHeader("X-OPERATION-ID") final String operationId,
                                             @RequestHeader(value = "X-TOKEN", required = false) final String token,
                                             @PathVariable("cpid") final String cpid,
                                             @RequestParam("stage") final String stage,
                                             @RequestBody final JsonNode jsonData) {
        final Params params = new Params();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setCpid(cpid);
        params.setStage(Stage.fromValue(stage).value());
        params.setProcessType("awardByBid");
        params.setOperationType("awardByBid");
        params.setToken(token);
        return startProcessResult(params, jsonData);
    }

    @RequestMapping(value = "/endOfStage/{cpid}", method = RequestMethod.POST)
    public ResponseEntity<String> endOfPSPQStage(@RequestHeader("Authorization") final String authorization,
                                                 @RequestHeader("X-OPERATION-ID") final String operationId,
                                                 @PathVariable("cpid") final String cpid,
                                                 @RequestParam("stage") final String stage) {
        final Params params = new Params();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setCpid(cpid);
        params.setStage(Stage.fromValue(stage).value());
        params.setProcessType("endOfPSPQStage");
        return startProcessResult(params, null);
    }
}

