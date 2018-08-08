package com.procurement.orchestrator.dao;

import com.procurement.orchestrator.domain.Rules;
import com.procurement.orchestrator.domain.entity.ContextEntity;
import com.procurement.orchestrator.domain.entity.OperationEntity;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.domain.entity.RequestEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface CassandraDao {

    void saveRequest(RequestEntity entity);

    Optional<RequestEntity> getRequestById(String requestId);

    Boolean saveOperationIfNotExist(OperationEntity entity);

    Boolean isOperationExist(String operationId);

    void saveOperationStep(OperationStepEntity entity);

    Optional<OperationStepEntity> getOperationStep(String processId, String taskId);

    void saveContext(ContextEntity entity);

    Optional<ContextEntity> getContextByCpId(String cpId);

    Optional<Rules> getRules(String country,
                             String pmd,
                             String processType);

}

