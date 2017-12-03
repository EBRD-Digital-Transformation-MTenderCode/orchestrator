package com.procurement.orchestrator.service;

import com.procurement.orchestrator.cassandra.OperationEntity;
import com.procurement.orchestrator.cassandra.OperationRepository;
import com.procurement.orchestrator.domain.constant.ResponseMessageType;
import com.procurement.orchestrator.exception.OperationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OperationServiceImpl implements OperationService {

    private final OperationRepository operationRepository;

    public OperationServiceImpl(final OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    @Override
    public void processOperation(final String transactionId, final String processType, final String dataProvider, final String jsonData) {
        if (operationRepository.getByTransactionId(transactionId)!=null) {
            throw new OperationException(ResponseMessageType.OPERATION_EXCEPTION.value());
        }
        operationRepository.save(getEntity(transactionId, dataProvider, processType, "new", jsonData));
    }

    private OperationEntity getEntity(final String transactionId,
                                      final String dataProvider,
                                      final String processType,
                                      final String state,
                                      final String jsonData) {
        final OperationEntity entity = new OperationEntity();
        entity.setTransactionId(transactionId);
        entity.setDate(LocalDateTime.now());
        entity.setDataProvider(dataProvider);
        entity.setProcessType(processType);
        entity.setState(state);
        entity.setJsonData(jsonData);
        return entity;
    }

}

