package com.procurement.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.domain.Rules;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.domain.entity.RequestEntity;
import com.procurement.orchestrator.domain.entity.StageEntity;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service
public interface OperationService {

    Boolean isRulesExist(Rules rules);

    void checkOperationById(String operationId);

    Boolean saveIfNotExist(String operationId, String processId);

    void saveFirstOperationStep(DelegateExecution execution, RequestEntity entity);

    OperationStepEntity getOperationStep(String processId, String taskId);

    OperationStepEntity getPreviousOperationStep(DelegateExecution execution);

    void saveOperationStep(DelegateExecution execution, OperationStepEntity entity, JsonNode request);

    void saveOperationStep(DelegateExecution execution, OperationStepEntity entity, Params params, JsonNode request);

    void saveOperationStep(DelegateExecution execution, OperationStepEntity entity, JsonNode request, JsonNode response);

    void saveOperationStep(DelegateExecution execution, OperationStepEntity entity, Params params, JsonNode request, JsonNode response);

    void saveOperationException(String processId, String taskId, String operationId, JsonNode request, JsonNode response);

    void saveStageParams(Params params);

    StageEntity getStageParams(String cpId, String processId);
}


