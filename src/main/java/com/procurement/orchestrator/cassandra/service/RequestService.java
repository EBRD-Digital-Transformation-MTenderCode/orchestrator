package com.procurement.orchestrator.cassandra.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.RequestEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface RequestService {

    void saveRequest(String txId, JsonNode jsonParams, JsonNode jsonData);

    Optional<RequestEntity> getRequest(String txId);

}
