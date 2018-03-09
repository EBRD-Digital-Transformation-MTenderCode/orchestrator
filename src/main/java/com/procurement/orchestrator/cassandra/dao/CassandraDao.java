package com.procurement.orchestrator.cassandra.dao;

import com.procurement.orchestrator.domain.entity.OperationEntity;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.domain.entity.RequestEntity;
import com.procurement.orchestrator.domain.entity.StageEntity;
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

    void saveStage(StageEntity entity);

    StageEntity getStageByCpId(String cpId);
}

