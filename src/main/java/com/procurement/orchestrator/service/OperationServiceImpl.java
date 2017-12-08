package com.procurement.orchestrator.service;

import com.procurement.orchestrator.cassandra.OperationEntity;
import com.procurement.orchestrator.cassandra.OperationRepository;
import com.procurement.orchestrator.cassandra.OperationValue;
import com.procurement.orchestrator.domain.constant.ResponseMessageType;
import com.procurement.orchestrator.exception.OperationException;
import com.procurement.orchestrator.utils.JsonUtil;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class OperationServiceImpl implements OperationService {

    private final OperationRepository operationRepository;

    private final JsonUtil jsonUtil;

    public OperationServiceImpl(final OperationRepository operationRepository,
                                final JsonUtil jsonUtil) {
        this.operationRepository = operationRepository;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void processFirstOperationStep(final String transactionId,
                                          final String platformId,
                                          final String processType,
                                          final String jsonData) {
        if (isTransactionExists(transactionId)) {
            throw new OperationException(ResponseMessageType.TRANSACTION_EXISTS.value());
        }
        processOperation(new OperationValue(transactionId, 1, "new", platformId, "yoda", processType, jsonData));
    }

    @Override
    public void processOperation(final OperationValue operation) {
        if (!getOperationByStep(operation.getTransactionId(), operation.getStep()).isPresent()) {
            operationRepository.save(getEntity(operation));
        }
    }

    @Override
    public Boolean isTransactionExists(final String transactionId) {
        return operationRepository.getOneById(transactionId)
                                  .isPresent();
    }

    @Override
    public Optional<OperationEntity> getOperationByStep(final String transactionId, final Integer step) {
        return operationRepository.getOneByStep(transactionId, step);
    }

    @Override
    public void saveOperation(final OperationValue operation) {
        operationRepository.save(getEntity(operation));
    }

    private OperationEntity getEntity(final OperationValue operation) {
        final HashMap<String, String> jsonData = jsonUtil.toObject(HashMap.class, operation.getJsonData());
        final OperationEntity entity = new OperationEntity();
        entity.setTransactionId(operation.getTransactionId());
        entity.setStep(operation.getStep());
        entity.setDate(LocalDateTime.now());
        entity.setDescription(operation.getDescription());
        entity.setDataProducer(operation.getDataProducer());
        entity.setDataConsumer(operation.getDataConsumer());
        entity.setProcessType(operation.getProcessType());
        entity.setJsonData(jsonUtil.toJson(jsonData));
        return entity;
    }
}

