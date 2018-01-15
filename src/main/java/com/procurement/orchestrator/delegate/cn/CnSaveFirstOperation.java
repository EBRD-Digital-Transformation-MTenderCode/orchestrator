package com.procurement.orchestrator.delegate.cn;

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
public class CnSaveFirstOperation implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CnSaveFirstOperation.class);

    private final OperationService operationService;

    private final RequestService requestService;

    public CnSaveFirstOperation(final RequestService requestService,
                                final OperationService operationService) {
        this.requestService = requestService;
        this.operationService = operationService;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info("->Save first operation.");
        final String txId = execution.getProcessBusinessKey();
        final Optional<RequestEntity> requestOptional = requestService.getRequest(txId);
        if (requestOptional.isPresent()) {
            final RequestEntity requestEntity = requestOptional.get();
            operationService.saveIfNotExist(requestEntity, execution.getProcessInstanceId());
        }
    }
}
