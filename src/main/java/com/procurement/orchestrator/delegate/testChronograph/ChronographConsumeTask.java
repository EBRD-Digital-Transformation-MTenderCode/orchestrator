package com.procurement.orchestrator.delegate.testChronograph;

import com.procurement.orchestrator.cassandra.model.OperationEntity;
import com.procurement.orchestrator.cassandra.model.RequestEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.cassandra.service.RequestService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ChronographConsumeTask implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ChronographConsumeTask.class);

//    private final MessageProducer messageProducer;

    private final OperationService operationService;

    private final RequestService requestService;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;

    public ChronographConsumeTask(
                                  //final MessageProducer messageProducer,
                                  final OperationService operationService,
                                  final RequestService requestService,
                                  final JsonUtil jsonUtil,
                                  final DateUtil dateUtil) {
 //       this.messageProducer = messageProducer;
        this.operationService = operationService;
        this.requestService = requestService;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info("->Data preparation for Chronograph.");
        final String txId = execution.getProcessBusinessKey();
        Optional<RequestEntity> requestOptional = requestService.getRequest(txId);
        if (requestOptional.isPresent()){
            RequestEntity requestEntity = requestOptional.get();
            operationService.saveIfNotExist(requestEntity, execution.getProcessInstanceId());
        }
    }
}

