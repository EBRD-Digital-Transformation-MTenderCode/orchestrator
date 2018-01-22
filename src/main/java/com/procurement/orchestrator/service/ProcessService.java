package com.procurement.orchestrator.service;

import java.util.Map;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

@Service
public interface ProcessService {


    ProcessInstance startProcess(String processType, String operationId);

    ProcessInstance startProcess(String processType, String operationId, Map<String, Object> variables);

    void processHttpException(int status, String error, String processId) throws BpmnError;

    void terminateProcess(String processId);
}

