package com.procurement.orchestrator.cassandra.service;

import com.procurement.orchestrator.cassandra.model.OperationEntity;
import com.procurement.orchestrator.cassandra.model.RequestEntity;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public interface OperationService {

    void saveOperation(OperationEntity entity);

    Optional<OperationEntity> getLastOperation(String txId);

    void checkOperationByTxId(String txId);

    void saveIfNotExist(RequestEntity requestEntity, String processId);
}
