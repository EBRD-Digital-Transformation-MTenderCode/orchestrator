package com.procurement.orchestrator.service;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

@Service
public interface ProcessService {

    ProcessInstance startProcess(String processType, String transactionId);
}
