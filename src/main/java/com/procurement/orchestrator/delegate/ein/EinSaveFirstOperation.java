package com.procurement.orchestrator.delegate.ein;

import com.procurement.orchestrator.cassandra.model.RequestEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.cassandra.service.RequestService;
import java.util.Optional;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EinSaveFirstOperation implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(EinSaveFirstOperation.class);

    private final OperationService operationService;

    private final RequestService requestService;

    public EinSaveFirstOperation(final RequestService requestService,
                                 final OperationService operationService) {
        this.requestService = requestService;
        this.operationService = operationService;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info("->Save first operation.");
        final String txId = execution.getProcessBusinessKey();
        Optional<RequestEntity> requestOptional = requestService.getRequest(txId);
        if (requestOptional.isPresent()) {
            RequestEntity requestEntity = requestOptional.get();
            operationService.saveIfNotExist(requestEntity, execution.getProcessInstanceId());
        }
    }
}
