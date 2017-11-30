package com.procurement.orchestrator.service;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

@Service
public class ProcessServiceImpl implements ProcessService {

    private final RuntimeService runtimeService;

    public ProcessServiceImpl(final RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @Override
    public ProcessInstance startProcessInstanceByKey(final String operationType, final String operationId) {
        ProcessInstance pi = runtimeService.startProcessInstanceByKey(operationType);
        return pi;
    }
}
