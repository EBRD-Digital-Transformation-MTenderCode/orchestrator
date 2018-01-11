package com.procurement.orchestrator.cassandra.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.RequestEntity;
import com.procurement.orchestrator.domain.Params;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public interface RequestService {

    void saveRequest(String txId, Params jsonParams, JsonNode jsonData);

    Optional<RequestEntity> getRequest(String txId);

}
