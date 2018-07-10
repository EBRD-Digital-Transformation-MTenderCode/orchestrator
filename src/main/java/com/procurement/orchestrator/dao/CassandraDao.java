package com.procurement.orchestrator.dao;

import com.procurement.orchestrator.domain.Rules;
import com.procurement.orchestrator.domain.entity.*;
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

    void saveContext(ContextEntity entity);

    Optional<ContextEntity> getContextByCpId(String cpId);

    Boolean isRulesExist(Rules rules);

    Optional<StageRulesEntity> getStageFromRules(String country, String pmd, String operationType);

}

