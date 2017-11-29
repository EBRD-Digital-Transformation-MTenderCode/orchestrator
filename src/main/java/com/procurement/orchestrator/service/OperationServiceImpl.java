package com.procurement.orchestrator.service;

import com.procurement.orchestrator.cassandra.OperationEntity;
import com.procurement.orchestrator.cassandra.OperationRepository;
import com.procurement.orchestrator.exception.OperationException;
import com.procurement.orchestrator.model.dto.ResponseMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OperationServiceImpl implements OperationService {

    private final OperationRepository operationRepository;

    public OperationServiceImpl(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    @Override
    public void processOperation(String operationId, String operationType, String jsonData) {
        if (operationRepository.findById(operationId).isPresent()) {
            throw new OperationException(ResponseMessage.OPERATION_EXCEPTION.value());
        }
        operationRepository.save(getEntity(operationId, operationType, jsonData));
    }

    private OperationEntity getEntity(String operationId, String operationType, String jsonData) {
        final OperationEntity entity = new OperationEntity();
        entity.setId(operationId);
        entity.setDate(LocalDateTime.now());
        entity.setType(operationType);
        entity.setJsonData(jsonData);
        return entity;
    }

}
