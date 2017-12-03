package com.procurement.orchestrator.service;

import com.procurement.orchestrator.cassandra.OperationEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface OperationService {

    Boolean isTransactionExists(String transactionId);

    void processFirstOperationStep(String transactionId,
                                   String platformId,
                                   String processType,
                                   String jsonData);

    void processOperation(String transactionId,
                          Integer step,
                          String description,
                          String dataProducer,
                          String dataConsumer,
                          String processType,
                          String jsonData);

    Optional<String> getOperationData(String transactionId, Integer step);

    Optional<OperationEntity> getOperationByStep(String transactionId, Integer step);

    void saveOperation(String transactionId,
                       Integer step,
                       String description,
                       String dataProducer,
                       String dataConsumer,
                       String processType,
                       String jsonData);

}
