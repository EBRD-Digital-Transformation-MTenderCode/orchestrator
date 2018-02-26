package com.procurement.orchestrator.controller;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.cassandra.service.RequestService;
import com.procurement.orchestrator.cassandra.model.Params;
import com.procurement.orchestrator.kafka.MessageProducer;
import com.procurement.orchestrator.kafka.dto.PlatformMessage;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import jdk.nashorn.internal.ir.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TenderController extends BaseController {

    private final MessageProducer messageProducer;

    public TenderController(final MessageProducer messageProducer,
                            final ProcessService processService,
                            final RequestService requestService,
                            final OperationService operationService,
                            final JsonUtil jsonUtil) {
        super(jsonUtil, requestService, operationService, processService);
        this.messageProducer = messageProducer;
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
        params.setProcessType("createCN");
        params.setOperationType("createCN");
        params.setCountry(country);
        params.setPmd(pmd);
        return startProcessResult(params, jsonData);
    }

    @RequestMapping(value = "/cn/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> updateCN(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @PathVariable("ocid") final String ocid,
                                           @RequestParam("country") final String country,
                                           @RequestParam("pmd") final String pmd,
                                           @RequestBody final JsonNode jsonData) {
        final Params params = new Params();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setCpid(ocid);
        params.setProcessType("updateCN");
        params.setOperationType("updateCN");
        params.setCountry(country);
        params.setPmd(pmd);
        return startProcessResult(params, jsonData);
    }


    @RequestMapping(value = "/bid", method = RequestMethod.POST)
    public ResponseEntity<String> createBid(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @RequestHeader(value = "X-TOKEN", required = false) final String token,
                                            @RequestParam("cpid") final String cpid,
                                            @RequestBody final JsonNode jsonData) {
        final Params params = new Params();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOperationId(operationId);
        params.setCpid(cpid);
        params.setStage("ps");
        params.setProcessType("submitTheBid");
        params.setOperationType("bid");
        params.setOwner(getOwner(authorization));
        params.setToken(token);
        return startProcessResult(params, jsonData);
    }

    @RequestMapping(value = "/enquiry", method = RequestMethod.POST)
    public ResponseEntity<String> enquiry(@RequestHeader("Authorization") final String authorization,
                                          @RequestHeader("X-OPERATION-ID") final String operationId,
                                          @RequestHeader(value = "X-TOKEN", required = false) final String token,
                                          @RequestParam("cpid") final String cpid,
                                          @RequestParam("ocid") final String ocid,
                                          @RequestParam("country") final String country,
                                          @RequestParam("pmd") final String pmd,
                                          @RequestBody final JsonNode jsonData) {
        final Params params = new Params();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setCpid(cpid);
        params.setOcid(ocid);
        params.setStage("ps");
        params.setProcessType("enquiry");
        params.setOperationType("createEnquiry");
        params.setCountry(country);
        params.setPmd(pmd);
        params.setToken(token);
        return startProcessResult(params, jsonData);
    }

    @RequestMapping(value = "/awardByBid", method = RequestMethod.POST)
    public ResponseEntity<String> awardByBid(@RequestHeader("Authorization") final String authorization,
                                             @RequestHeader("X-OPERATION-ID") final String operationId,
                                             @RequestHeader(value = "X-TOKEN", required = false) final String token,
                                             @RequestParam("cpid") final String cpid,
                                             @RequestBody final JsonNode jsonData) {
        final Params params = new Params();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setCpid(cpid);
        params.setStage("ps");
        params.setProcessType("awardByBid");
        params.setOperationType("awardByBid");
        params.setToken(token);
        return startProcessResult(params, jsonData);
    }

    @RequestMapping(value = "/endOfPSPQStage", method = RequestMethod.POST)
    public ResponseEntity<String> endOfPSPQStage(@RequestHeader("Authorization") final String authorization,
                                                 @RequestHeader("X-OPERATION-ID") final String operationId,
                                                 @RequestHeader(value = "X-TOKEN", required = false) final String token,
                                                 @RequestParam("cpid") final String cpid,
                                                 @RequestBody final JsonNode jsonData) {
        final Params params = new Params();
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOwner(getOwner(authorization));
        params.setOperationId(operationId);
        params.setCpid(cpid);
        params.setStage("ps");
        params.setProcessType("endOfPSPQStage");
        params.setOperationType("endOfPSPQStage");
        params.setToken(token);
        return startProcessResult(params, jsonData);
    }

    @RequestMapping(value = "/sendMessage", method = RequestMethod.POST)
    public ResponseEntity<String> sendMessage(@RequestHeader("X-OPERATION-ID") final String operationId) {
        final PlatformMessage message = new PlatformMessage(
                true,
                operationId,
                null,
                null,
                null,
                null);
        messageProducer.sendToPlatform(message);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/tenderPeriodEnd", method = RequestMethod.POST)
    public ResponseEntity<String> endOfPSPQStage(@RequestHeader("Authorization") final String authorization,
                                                 @RequestParam("cpid") final String cpid,
                                                 @RequestParam("stage") final String stage,
                                                 @RequestParam("country") final String country,
                                                 @RequestParam("pmd") final String pmd) {
        final Params params = new Params();
        params.setProcessType("tenderPeriodEnd");
        params.setCpid(cpid);
        params.setStage(stage);
        params.setOwner(getOwner(authorization));
        params.setCountry(country);
        params.setPmd(pmd);
        params.setRequestId(UUIDs.timeBased().toString());
        params.setOperationId(params.getRequestId());
        JsonUtil util = new JsonUtil(new ObjectMapper());
        return startProcessResult(params, util.createObjectNode());
    }
}

