package com.procurement.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.domain.entity.RequestEntity;
import org.springframework.stereotype.Service;

@Service
public interface RequestService {

    void saveRequest(String requestId, String operationId, Params jsonParams, JsonNode jsonData);

    RequestEntity getRequestById(String requestId, String processId);

}
