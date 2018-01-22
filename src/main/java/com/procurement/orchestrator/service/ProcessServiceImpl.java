package com.procurement.orchestrator.service;

import java.util.Map;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

@Service
public class ProcessServiceImpl implements ProcessService {

    private final RuntimeService runtimeService;

    public ProcessServiceImpl(final RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @Override
    public ProcessInstance startProcess(final String processType, final String operationId) {
        return runtimeService.startProcessInstanceByKey(processType, operationId);
    }

    @Override
    public ProcessInstance startProcess(String processType, String operationId, Map<String, Object> variables) {
        return runtimeService.startProcessInstanceByKey(processType, operationId, variables);
    }

    @Override
    public void processHttpException(final int status,
                                     final String error,
                                     final String processId) throws BpmnError {
        if (status >= 400 && status < 500) {
            throw new BpmnError("TR_EXCEPTION", error);
        } else {
            runtimeService.suspendProcessInstanceById(processId);
        }
    }

    @Override
    public void terminateProcess(String processId) {
        runtimeService.deleteProcessInstance(processId, "Removal of the backward process.");
    }
}
