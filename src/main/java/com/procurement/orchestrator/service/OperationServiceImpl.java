package com.procurement.orchestrator.service;

import com.procurement.orchestrator.cassandra.OperationEntity;
import com.procurement.orchestrator.cassandra.OperationRepository;
import com.procurement.orchestrator.domain.constant.ResponseMessageType;
import com.procurement.orchestrator.exception.OperationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class OperationServiceImpl implements OperationService {

    private final OperationRepository operationRepository;

    public OperationServiceImpl(final OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    @Override
    public void processFirstOperationStep(final String transactionId,
                                          final String platformId,
                                          final String processType,
                                          final String jsonData) {
        if (isTransactionExists(transactionId)) {
            throw new OperationException(ResponseMessageType.TRANSACTION_EXISTS.value());
        }
        processOperation(transactionId, 1, "new", platformId, "yoda", processType, jsonData);
    }

    @Override
    public void processOperation(final String transactionId,
                                 final Integer step,
                                 final String description,
                                 final String dataProducer,
                                 final String dataConsumer,
                                 final String processType,
                                 final String jsonData) {
        if (!getOperationByStep(transactionId, step).isPresent()) {
            operationRepository.save(getEntity(transactionId, step, description, dataProducer, dataConsumer, processType, jsonData));
        }
    }


    @Override
    public Optional<String> getOperationData(String transactionId, Integer step) {
        Optional<String> result = Optional.empty();
        Optional<OperationEntity> entityOptional = operationRepository.getOneByStep(transactionId, step);
        if (entityOptional.isPresent()) {
            OperationEntity entity = entityOptional.get();
            String jsonData = entity.getJsonData();
            if (!jsonData.isEmpty()) {
                return Optional.of(jsonData);
            }
        }
        return result;
    }

    @Override
    public Boolean isTransactionExists(String transactionId) {
        return operationRepository.getOneById(transactionId).isPresent();
    }

    @Override
    public Optional<OperationEntity> getOperationByStep(String transactionId, Integer step) {
        return operationRepository.getOneByStep(transactionId, step);
    }

    @Override
    public void saveOperation(String transactionId, Integer step, String description, String dataProducer, String dataConsumer, String processType, String jsonData) {
        operationRepository.save(getEntity(transactionId, step, description, dataProducer, dataConsumer, processType, jsonData));
    }

    private OperationEntity getEntity(final String transactionId,
                                      final Integer step,
                                      final String description,
                                      final String dataProducer,
                                      final String dataConsumer,
                                      final String processType,
                                      final String jsonData) {
        final OperationEntity entity = new OperationEntity();
        entity.setTransactionId(transactionId);
        entity.setStep(step);
        entity.setDate(LocalDateTime.now());
        entity.setDescription(description);
        entity.setDataProducer(dataProducer);
        entity.setDataConsumer(dataConsumer);
        entity.setProcessType(processType);
        entity.setJsonData(jsonData);
        return entity;
    }

}

