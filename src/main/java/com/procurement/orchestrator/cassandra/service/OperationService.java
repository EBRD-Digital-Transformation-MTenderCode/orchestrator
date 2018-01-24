package com.procurement.orchestrator.cassandra.service;

import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.domain.Params;
import java.util.Optional;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service
public interface OperationService {

    void checkOperationById(String operationId);

    Boolean saveIfNotExist(String operationId, String processId);

    void saveFirstOperationStep(OperationStepEntity entity);

    Optional<OperationStepEntity> getOperationStep(String processId, String taskId);

    void saveOperationStep(String taskId, OperationStepEntity entity, Params params, Object response);

    void saveOperationStep(String taskId, OperationStepEntity entity, Object response);

    void saveOperationStep(String taskId, OperationStepEntity entity);
}
