package com.procurement.orchestrator.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.cassandra.service.RequestService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/do")
public class BaseController {

    private final JsonUtil jsonUtil;
    private final RequestService requestService;
    private final OperationService operationService;
    private final ProcessService processService;

    public BaseController(final JsonUtil jsonUtil,
                          final RequestService requestService,
                          final OperationService operationService,
                          final ProcessService processService) {
        this.jsonUtil = jsonUtil;
        this.requestService = requestService;
        this.operationService = operationService;
        this.processService = processService;
    }


    ResponseEntity<String> startProcessResult(final Params params, final JsonNode jsonData) {
        saveRequest(params.getRequestId(), params.getOperationId(), params, jsonData);
        checkOperationById(params.getOperationId());
        Map<String, Object> variables = new HashMap<>();
        variables.put("requestId", params.getRequestId());
        variables.put("isTokenPresent", ((params.getToken() == null || "".equals(params.getToken().trim())) ? 0 : 1));
        startProcess(params.getProcessType(), params.getOperationId(), variables);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    String getOwner(String authorization) {
        final String[] split = authorization.split("\\.");
        final String payload = split[1];
        final String encodedToken = StringUtils.newStringUtf8(Base64.decodeBase64(payload.getBytes()));
        JsonNode jsonNode = jsonUtil.toJsonNode(encodedToken);
        return jsonNode.get("idPlatform").asText();
    }

    void saveRequest(final String requestId,
                     final String operationId,
                     final Params params,
                     final JsonNode jsonData) {
        requestService.saveRequest(requestId, operationId, params, jsonData);
    }

    void checkOperationById(final String operationId) {
        operationService.checkOperationById(operationId);
    }

    void startProcess(final String processType,
                            final String operationId,
                            final Map<String, Object> variables) {
        processService.startProcess(processType, operationId, variables);
    }

}
