package com.procurement.orchestrator.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.RequestService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/do")
public class BaseController {

    private final JsonUtil jsonUtil;
    private final RequestService requestService;
    private final OperationService operationService;

    public BaseController(final JsonUtil jsonUtil,
                          final RequestService requestService,
                          final OperationService operationService) {
        this.jsonUtil = jsonUtil;
        this.requestService = requestService;
        this.operationService = operationService;
    }

    void saveRequestAndCheckOperation(final Context context, final JsonNode jsonData) {
        final JsonNode data;
        if (Objects.isNull(jsonData)) {
            data = jsonUtil.createObjectNode();
        } else {
            data = jsonData;
        }
        requestService.saveRequest(context.getRequestId(), context.getOperationId(), context, data);
        operationService.checkOperationById(context.getOperationId());
    }

    String getOwner(final String authorization) {
        final String[] split = authorization.split("\\.");
        final String payload = split[1];
        final String encodedToken = StringUtils.newStringUtf8(Base64.decodeBase64(payload.getBytes()));
        final JsonNode jsonNode = jsonUtil.toJsonNode(encodedToken);
        return jsonNode.get("idPlatform").asText();
    }
}
