package com.procurement.orchestrator.service;

import com.procurement.orchestrator.cassandra.OperationEntity;
import com.procurement.orchestrator.cassandra.OperationRepository;
import com.procurement.orchestrator.exception.OperationException;
import com.procurement.orchestrator.model.dto.ResponseMessage;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class OperationServiceImpl implements OperationService {

    private final OperationRepository operationRepository;

    public OperationServiceImpl(final OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    @Override
    public void processOperation(final String operationId, final String operationType, final String jsonData) {
        if (operationRepository.findById(operationId).isPresent()) {
            throw new OperationException(ResponseMessage.OPERATION_EXCEPTION.value());
        }
        operationRepository.save(getEntity(operationId, operationType, jsonData));
    }

    private OperationEntity getEntity(final String operationId, final String operationType, final String jsonData) {
        final OperationEntity entity = new OperationEntity();
        entity.setId(operationId);
        entity.setDate(LocalDateTime.now());
        entity.setType(operationType);
        entity.setJsonData(jsonData);
        return entity;
    }

}

