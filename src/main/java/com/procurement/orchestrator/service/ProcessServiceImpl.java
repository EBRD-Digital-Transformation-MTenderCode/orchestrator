package com.procurement.orchestrator.service;

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
    public ProcessInstance startProcess(final String processType, final String transactionId) {
        return runtimeService.startProcessInstanceByKey(processType, transactionId);
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
}
