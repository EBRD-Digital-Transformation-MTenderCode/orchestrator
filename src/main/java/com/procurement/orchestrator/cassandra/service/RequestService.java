package com.procurement.orchestrator.cassandra.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.entity.RequestEntity;
import com.procurement.orchestrator.domain.Params;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public interface RequestService {

    void saveRequest(String requestId, String operationId, Params jsonParams, JsonNode jsonData);

    Optional<RequestEntity> getRequestById(String requestId);

}
