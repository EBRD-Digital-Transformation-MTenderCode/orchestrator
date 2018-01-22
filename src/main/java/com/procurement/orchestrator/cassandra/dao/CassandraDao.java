package com.procurement.orchestrator.cassandra.dao;

import com.procurement.orchestrator.cassandra.model.OperationEntity;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.model.RequestEntity;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public interface CassandraDao {

    void saveRequest(RequestEntity entity);

    Optional<RequestEntity> getRequestById(String requestId);

    Boolean saveOperationIfNotExist(OperationEntity entity);

    Boolean isOperationExist(String operationId);

    void saveOperationStep(OperationStepEntity entity);

    Optional<OperationStepEntity> getOperationStep(String processId, String taskId);
}

