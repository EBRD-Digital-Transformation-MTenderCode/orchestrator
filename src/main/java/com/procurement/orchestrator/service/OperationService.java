package com.procurement.orchestrator.service;

import com.procurement.orchestrator.cassandra.OperationEntity;
import com.procurement.orchestrator.cassandra.OperationValue;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public interface OperationService {

    Boolean isTransactionExists(String transactionId);

    void processFirstOperationStep(String transactionId,
                                   String platformId,
                                   String processType,
                                   String jsonData);

    void processOperation(OperationValue operation);

    Optional<OperationEntity> getOperationByStep(String transactionId, Integer step);

    void saveOperation(OperationValue operation);

}
