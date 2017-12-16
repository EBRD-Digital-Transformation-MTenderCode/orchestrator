package com.procurement.orchestrator.cassandra;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.constant.ResponseMessageType;
import com.procurement.orchestrator.exception.OperationException;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class OperationServiceImpl implements OperationService {

    private final OperationRepository operationRepository;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;

    public OperationServiceImpl(final OperationRepository operationRepository,
                                final JsonUtil jsonUtil,
                                final DateUtil dateUtil) {
        this.operationRepository = operationRepository;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
    }

    @Override
    public void processFirstOperationStep(final String transactionId,
                                          final String platformId,
                                          final String processType,
                                          final String jsonData) {
        if (isTransactionExists(transactionId)) {
            throw new OperationException(ResponseMessageType.TRANSACTION_EXISTS.value());
        }
        processOperation(new OperationValue(transactionId, 1, "new from platform", platformId, "yoda", processType,
                                            jsonData));
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
        final JsonNode jsonData = jsonUtil.toJsonNode(operation.getJsonData());
        final OperationEntity entity = new OperationEntity();
        entity.setTransactionId(operation.getTransactionId());
        entity.setStep(operation.getStep());
        entity.setDate(dateUtil.getNowUTC());
        entity.setDescription(operation.getDescription());
        entity.setDataProducer(operation.getDataProducer());
        entity.setDataConsumer(operation.getDataConsumer());
        entity.setProcessType(operation.getProcessType());
        entity.setJsonData(jsonUtil.toJson(jsonData));
        return entity;
    }
}

