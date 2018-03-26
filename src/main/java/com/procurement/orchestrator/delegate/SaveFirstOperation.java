package com.procurement.orchestrator.delegate;

import com.procurement.orchestrator.domain.entity.RequestEntity;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.service.RequestService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SaveFirstOperation implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(SaveFirstOperation.class);

    private final OperationService operationService;

    private final ProcessService processService;

    private final RequestService requestService;


    public SaveFirstOperation(final RequestService requestService,
                              final OperationService operationService,
                              final ProcessService processService) {
        this.requestService = requestService;
        this.operationService = operationService;
        this.processService = processService;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
        final String requestId = (String) execution.getVariable("requestId");
        final String operationId = execution.getProcessBusinessKey();
        final String processId = execution.getProcessInstanceId();
        final RequestEntity request = requestService.getRequestById(requestId, processId);
        if (!operationService.saveIfNotExist(operationId, processId)) {
            processService.terminateProcess(processId, "operationId: " + operationId + " already exist.");
        }
        operationService.saveFirstOperationStep(execution, request);
    }
}
