package com.procurement.orchestrator.cassandra.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.domain.entity.RequestEntity;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.domain.entity.StageEntity;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service
public interface OperationService {

    void checkOperationById(String operationId);

    Boolean saveIfNotExist(String operationId, String processId);

    void saveFirstOperationStep(DelegateExecution execution, RequestEntity entity);

    OperationStepEntity getOperationStep(String processId, String taskId);

    OperationStepEntity getPreviousOperationStep(DelegateExecution execution);

    void saveOperationStep(DelegateExecution execution, OperationStepEntity entity);

    void saveOperationStep(DelegateExecution execution, OperationStepEntity entity, Params params);

    void saveOperationStep(DelegateExecution execution, OperationStepEntity entity, JsonNode response);

    void saveOperationStep(DelegateExecution execution, OperationStepEntity entity, Params params, JsonNode response);

    void saveOperationException(String processId, String taskId, JsonNode response);

    void saveStage(Params params);

    StageEntity getStageParams(String cpId, String processId);
}
