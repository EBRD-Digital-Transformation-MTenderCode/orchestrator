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

    Optional<OperationStepEntity> getOperationStep(DelegateExecution execution);

    void saveOperationStep(DelegateExecution execution, OperationStepEntity entity, Params params, Object response);

    void saveOperationStep(DelegateExecution execution, OperationStepEntity entity, Object response);

    void saveOperationStep(DelegateExecution execution, OperationStepEntity entity);
}
