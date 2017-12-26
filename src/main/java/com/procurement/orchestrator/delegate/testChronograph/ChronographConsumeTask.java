package com.procurement.orchestrator.delegate.testChronograph;

import com.procurement.orchestrator.cassandra.OperationService;
import com.procurement.orchestrator.cassandra.OperationValue;
import com.procurement.orchestrator.kafka.Task;
import com.procurement.orchestrator.kafka.MessageProducer;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ChronographConsumeTask implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ChronographConsumeTask.class);

    private final MessageProducer messageProducer;

    private final OperationService operationService;

    private final JsonUtil jsonUtil;

    public ChronographConsumeTask(final MessageProducer messageProducer,
                                  final OperationService operationService,
                                  final JsonUtil jsonUtil) {
        this.messageProducer = messageProducer;
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info("->Data preparation for Chronograph.");
        final String transactionId = execution.getProcessBusinessKey();
        final String getProcessType = execution.getProcessDefinitionId();
        Task task = new Task(transactionId, getProcessType);
        final OperationValue operation = new OperationValue(
                transactionId,
                2,
                "consume task for chronograph",
                "chronograph",
                "yoda",
                getProcessType,
                jsonUtil.toJson(task));

        operationService.saveOperation(operation);
    }
}

