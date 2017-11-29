package com.procurement.orchestrator.service;

import org.springframework.stereotype.Service;

@Service
public interface OperationService {

    void processOperation(String operationId, String operationType, String jsonData);
}
