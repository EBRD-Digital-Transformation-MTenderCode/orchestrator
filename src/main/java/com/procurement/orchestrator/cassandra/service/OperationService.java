package com.procurement.orchestrator.cassandra.service;

import com.procurement.orchestrator.cassandra.model.OperationEntity;
import com.procurement.orchestrator.cassandra.model.RequestEntity;
import com.procurement.orchestrator.domain.Params;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public interface OperationService {

    void saveOperation(OperationEntity entity);

    Optional<OperationEntity> getLastOperation(String txId);

    void checkOperationByTxId(String txId);

    void saveIfNotExist(RequestEntity requestEntity, String processId);

    void processResponse(OperationEntity entity, Params params, Object response);

    void processResponse(OperationEntity entity, Object response);

    void processResponse(OperationEntity entity);
}
