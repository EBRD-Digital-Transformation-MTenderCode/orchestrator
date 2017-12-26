package com.procurement.orchestrator.delegate.testChronograph;

import com.procurement.orchestrator.cassandra.OperationService;
import com.procurement.orchestrator.cassandra.OperationValue;
import com.procurement.orchestrator.kafka.Message;
import com.procurement.orchestrator.kafka.MessageProducer;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ChronographProduceTask implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ChronographProduceTask.class);

    private final MessageProducer messageProducer;

    private final OperationService operationService;

    private final JsonUtil jsonUtil;

    public ChronographProduceTask(final MessageProducer messageProducer,
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
        final String getProcessType = "testChronograph";
        Message message = new Message(transactionId, getProcessType);

        messageProducer.send(message);

        final OperationValue operation = new OperationValue(
                transactionId,
                1,
                "produce task for chronograph",
                "yoda",
                "chronograph",
                getProcessType,
                jsonUtil.toJson(message));

        operationService.saveOperation(operation);
    }
}

