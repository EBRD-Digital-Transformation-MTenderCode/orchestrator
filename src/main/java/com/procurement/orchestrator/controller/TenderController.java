package com.procurement.orchestrator.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.ProcurementMethod;
import com.procurement.orchestrator.exception.OperationException;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.service.RequestService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TenderController extends DoBaseController {
    private static final Logger LOG = LoggerFactory.getLogger(TenderController.class);

    private final DateUtil dateUtil;
    private final ProcessService processService;
    private final RequestService requestService;
    private final JsonUtil jsonUtil;

    public TenderController(final ProcessService processService,
                            final RequestService requestService,
                            final JsonUtil jsonUtil,
                            final DateUtil dateUtil) {
        this.dateUtil = dateUtil;
        this.jsonUtil = jsonUtil;
        this.processService = processService;
        this.requestService = requestService;
    }

    @RequestMapping(value = "/cn", method = RequestMethod.POST)
    public ResponseEntity<String> createCN(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestParam("country") final String country,
                                           @RequestParam("pmd") final String pmd,
                                           @RequestParam(value = "testMode", defaultValue = "false") final boolean testMode,
                                           @RequestBody final JsonNode data) {
        requestService.validate(operationId, data);
        final Context context = requestService.getContextForCreate(authorization, operationId, country, pmd, "createCN", testMode);
        processService.setEnquiryPeriodStartDate(data, context.getStartDate(), null);
        processService.setTenderPeriodStartDate(data, processService.getEnquiryPeriodEndDate(data, null), null);
        requestService.saveRequestAndCheckOperation(context, data);
        processService.startProcess(context, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/cn/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> updateCN(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestHeader("X-TOKEN") final String token,
                                           @PathVariable("cpid") final String cpid,
                                           @PathVariable("ocid") final String ocid,
                                           @RequestBody final JsonNode data) {
        requestService.validate(operationId, data);
        final String processType = getUpdateCnProcessType(cpid);
        final Context context = requestService.getContextForUpdate(authorization, operationId, cpid, ocid, token, processType);
        processService.setEnquiryPeriodStartDate(data, context.getStartDate(), null);
        processService.setTenderPeriodStartDate(data, processService.getEnquiryPeriodEndDate(data, null), null);
        requestService.saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", context.getOperationType());
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    private String getUpdateCnProcessType(String cpid) {
        final Context prevContext = requestService.getContext(cpid);
        final ProcurementMethod pmd = ProcurementMethod.valueOf(prevContext.getPmd());
        String processType;
        switch (pmd) {
            case OT:
            case TEST_OT:
            case SV:
            case TEST_SV:
            case MV:
            case TEST_MV:
                processType = "updateCN";
                break;

            case DA:
            case TEST_DA:
            case NP:
            case TEST_NP:
            case OP:
            case TEST_OP:
                processType = "updateCnOp";
                break;

            default:
                throw new OperationException("Invalid previous pmd: '" + pmd + "'");
        }
        return processType;
    }

    @RequestMapping(value = "/pn", method = RequestMethod.POST)
    public ResponseEntity<String> createPN(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestParam("country") final String country,
                                           @RequestParam("pmd") final String pmd,
                                           @RequestParam(value = "testMode", defaultValue = "false") final boolean testMode,
                                           @RequestBody final JsonNode data) {
        requestService.validate(operationId, data);
        final Context context = requestService.getContextForCreate(authorization, operationId, country, pmd, "createPN", testMode);
        context.setEndDate(processService.getTenderPeriodEndDate(data, null));
        requestService.saveRequestAndCheckOperation(context, data);
        processService.startProcess(context, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/pn/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> updatePN(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestHeader("X-TOKEN") final String token,
                                           @PathVariable("cpid") final String cpid,
                                           @PathVariable("ocid") final String ocid,
                                           @RequestBody final JsonNode data) {
        requestService.validate(operationId, data);
        final Context context = requestService.getContextForUpdate(authorization, operationId, cpid, ocid, token, "updatePN");
        context.setEndDate(processService.getTenderPeriodEndDate(data, null));
        requestService.saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", context.getOperationType());
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/bid/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> createBid(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @PathVariable("cpid") final String cpid,
                                            @PathVariable("ocid") final String ocid,
                                            @RequestBody final JsonNode data) {
        requestService.validate(operationId, data);
        final Context context = requestService.getContextForUpdate(authorization, operationId, cpid, ocid, null, "createBid");
        context.setOperationType("createBid");
        requestService.saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", "createBid");
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/bid/{cpid}/{ocid}/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> updateBid(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @RequestHeader("X-TOKEN") final String token,
                                            @PathVariable("cpid") final String cpid,
                                            @PathVariable("ocid") final String ocid,
                                            @PathVariable("id") final String id,
                                            @RequestBody final JsonNode data) {
        requestService.validate(operationId, data);
        final Context context = requestService.getContextForUpdate(authorization, operationId, cpid, ocid, token, "updateBid");
        context.setId(id);
        context.setOperationType("updateBid");
        requestService.saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", "updateBid");
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/enquiry/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> createEnquiry(@RequestHeader("Authorization") final String authorization,
                                                @RequestHeader("X-OPERATION-ID") final String operationId,
                                                @PathVariable("cpid") final String cpid,
                                                @PathVariable("ocid") final String ocid,
                                                @RequestBody final JsonNode data) {
        requestService.validate(operationId, data);
        final Context context = requestService.getContextForUpdate(authorization, operationId, cpid, ocid, null, "enquiry");
        requestService.saveRequestAndCheckOperation(context, data);
        processService.startProcess(context, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/enquiry/{cpid}/{ocid}/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> addAnswer(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @RequestHeader("X-TOKEN") final String token,
                                            @PathVariable("cpid") final String cpid,
                                            @PathVariable("ocid") final String ocid,
                                            @PathVariable("id") final String id,
                                            @RequestBody final JsonNode data) {
        requestService.validate(operationId, data);
        final Context context = requestService.getContextForUpdate(authorization, operationId, cpid, ocid, token, "answer");
        context.setId(id);
        requestService.saveRequestAndCheckOperation(context, data);
        processService.startProcess(context, new HashMap<>());
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/award/{cpid}/{ocid}/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> awardByBid(@RequestHeader("Authorization") final String authorization,
                                             @RequestHeader("X-OPERATION-ID") final String operationId,
                                             @RequestHeader("X-TOKEN") final String token,
                                             @PathVariable("cpid") final String cpid,
                                             @PathVariable("ocid") final String ocid,
                                             @PathVariable("id") final String id,
                                             @RequestBody final JsonNode data) {
        requestService.validate(operationId, data);
        final Context prevContext = requestService.getContext(cpid);
        final String process;
        if("NP".equals(prevContext.getStage()))
            process = "evaluateAward";
        else
            process = "awardByBid";

        final Context context = requestService.getContextForUpdate(authorization, operationId, cpid, ocid, token, process);
        context.setId(id);
        requestService.saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", context.getOperationType());
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/award/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> createAward(@RequestHeader("Authorization") final String authorization,
                                             @RequestHeader("X-OPERATION-ID") final String operationId,
                                             @RequestHeader("X-TOKEN") final String token,
                                             @PathVariable("cpid") final String cpid,
                                             @PathVariable("ocid") final String ocid,
                                             @RequestParam("lotId") final String lotId,
                                             @RequestBody final String body) {
        if(LOG.isDebugEnabled())
            LOG.debug("Received request to endpoint '/award/{cpid}/{ocid}?lotId' (operation id: '{}', cpid: '{}', ocid: '{}', lot id: '{}', body: '{}').", operationId, cpid, ocid, lotId, body);

        final JsonNode data = jsonUtil.toJsonNode(body);
        requestService.validate(operationId, data);
        final Context context = requestService.getContextForUpdate(authorization, operationId, cpid, ocid, token, "createAward");
        context.setId(lotId);
        requestService.saveRequestAndCheckOperation(context, data);

        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", context.getOperationType());
        variables.put("lotId", context.getId());
        processService.startProcess(context, variables);

        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/protocol/{cpid}/{ocid}/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> createCan(@RequestHeader("Authorization") final String authorization,
                                            @RequestHeader("X-OPERATION-ID") final String operationId,
                                            @RequestHeader("X-TOKEN") final String token,
                                            @PathVariable("cpid") final String cpid,
                                            @PathVariable("ocid") final String ocid,
                                            @PathVariable("id") final String lotId) {
        requestService.validate(operationId, null);
        final Context context = requestService.getContextForUpdate(authorization, operationId, cpid, ocid, token, "createCan");
        context.setId(lotId);
        requestService.saveRequestAndCheckOperation(context, null);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", context.getOperationType());
        variables.put("lotId", context.getId());
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/document/bid/{cpid}/{ocid}/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> bidDocs(@RequestHeader("Authorization") final String authorization,
                                          @RequestHeader("X-OPERATION-ID") final String operationId,
                                          @RequestHeader("X-TOKEN") final String token,
                                          @PathVariable("cpid") final String cpid,
                                          @PathVariable("ocid") final String ocid,
                                          @PathVariable("id") final String id,
                                          @RequestBody final JsonNode data) {
        requestService.validate(operationId, data);
        final Context context = requestService.getContextForUpdate(authorization, operationId, cpid, ocid, token, "updateBidDocs");
        context.setId(id);
        context.setOperationType("updateBidDocs");
        requestService.saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", "updateBidDocs");
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/document/can/{cpid}/{ocid}/{canid}", method = RequestMethod.POST)
    public ResponseEntity<String> updateDocuments(@RequestHeader("Authorization") final String authorization,
                                                  @RequestHeader("X-OPERATION-ID") final String operationId,
                                                  @RequestHeader("X-TOKEN") final String token,
                                                  @PathVariable("cpid") final String cpid,
                                                  @PathVariable("ocid") final String ocid,
                                                  @PathVariable("canid") final String canid,
                                                  @RequestBody final JsonNode data) {
        requestService.validate(operationId, null);
        final Context context = requestService.getContextForUpdate(authorization, operationId,
                cpid, ocid, token, "updateCanDocs");
        context.setId(canid);
        requestService.saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", context.getOperationType());
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/confirmation/can/{cpid}/{ocid}/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> confirmationCan(@RequestHeader("Authorization") final String authorization,
                                                  @RequestHeader("X-OPERATION-ID") final String operationId,
                                                  @RequestHeader("X-TOKEN") final String token,
                                                  @PathVariable("cpid") final String cpid,
                                                  @PathVariable("ocid") final String ocid,
                                                  @PathVariable("id") final String id) {
        requestService.validate(operationId, null);
        final Context context = requestService.getContextForUpdate(authorization, operationId, cpid, ocid, token, "confirmCan");
        context.setId(id);
        requestService.saveRequestAndCheckOperation(context, null);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", context.getOperationType());
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/contract/{cpid}/{ocid}", method = RequestMethod.POST)
    public ResponseEntity<String> createAC(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestHeader("X-TOKEN") final String token,
                                           @PathVariable("cpid") final String cpid,
                                           @PathVariable("ocid") final String ocid,
                                           @RequestBody final JsonNode data) {
        requestService.validate(operationId, data);
        final Context context = requestService.getContextForUpdate(authorization, operationId, cpid, ocid, token, "createAC");
        requestService.saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", context.getOperationType());
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/contract/{cpid}/{ocid}/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> updateAC(@RequestHeader("Authorization") final String authorization,
                                           @RequestHeader("X-OPERATION-ID") final String operationId,
                                           @RequestHeader("X-TOKEN") final String token,
                                           @PathVariable("cpid") final String cpid,
                                           @PathVariable("ocid") final String ocid,
                                           @PathVariable("id") final String id,
                                           @RequestBody final JsonNode data) {
        requestService.validate(operationId, data);
        final Context context = requestService.getContextForContractUpdate(authorization, operationId,
                cpid, ocid, token, "updateAC");
        requestService.saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", context.getOperationType());
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/confirmation/buyer/{cpid}/{ocid}/{requestID}", method = RequestMethod.POST)
    public ResponseEntity<String> buyerSigningAC(@RequestHeader("Authorization") final String authorization,
                                                 @RequestHeader("X-OPERATION-ID") final String operationId,
                                                 @RequestHeader("X-TOKEN") final String token,
                                                 @PathVariable("cpid") final String cpid,
                                                 @PathVariable("ocid") final String ocid,
                                                 @PathVariable("requestID") final String requestID,
                                                 @RequestBody final JsonNode data) {
        requestService.validate(operationId, data);
        final Context context = requestService.getContextForContractUpdate(
                authorization, operationId, cpid, ocid, token, "buyerSigningAC");
        context.setId(requestID);
        requestService.saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", context.getOperationType());
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/confirmation/tenderer/{cpid}/{ocid}/{requestID}", method = RequestMethod.POST)
    public ResponseEntity<String> tendererSigningAC(@RequestHeader("Authorization") final String authorization,
                                                    @RequestHeader("X-OPERATION-ID") final String operationId,
                                                    @RequestHeader("X-TOKEN") final String token,
                                                    @PathVariable("cpid") final String cpid,
                                                    @PathVariable("ocid") final String ocid,
                                                    @PathVariable("requestID") final String requestID,
                                                    @RequestBody final JsonNode data) {
        requestService.validate(operationId, data);
        final Context context = requestService.getContextForContractUpdate(
                authorization, operationId, cpid, ocid, token, "supplierSigningAC");
        context.setId(requestID);
        requestService.saveRequestAndCheckOperation(context, data);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("operationType", context.getOperationType());
        processService.startProcess(context, variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

}