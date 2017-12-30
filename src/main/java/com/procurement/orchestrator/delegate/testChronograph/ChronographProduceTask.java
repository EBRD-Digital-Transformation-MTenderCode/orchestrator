package com.procurement.orchestrator.delegate.testChronograph;

import com.procurement.orchestrator.cassandra.OperationService;
import com.procurement.orchestrator.cassandra.OperationValue;
import com.procurement.orchestrator.kafka.Task;
import com.procurement.orchestrator.kafka.MessageProducer;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ChronographProduceTask implements JavaDelegate {

//    private static final Logger LOG = LoggerFactory.getLogger(ChronographProduceTask.class);
//
//    private final MessageProducer messageProducer;
//
//    private final OperationService operationService;
//
//    private final JsonUtil jsonUtil;
//
//    private final DateUtil dateUtil;
//
//    public ChronographProduceTask(final MessageProducer messageProducer,
//                                  final OperationService operationService,
//                                  final JsonUtil jsonUtil,
//                                  final DateUtil dateUtil) {
//        this.messageProducer = messageProducer;
//        this.operationService = operationService;
//        this.jsonUtil = jsonUtil;
//        this.dateUtil = dateUtil;
//    }

    @Override
    public void execute(final DelegateExecution execution) {
//        LOG.info("->Data preparation for Chronograph.");
//        final String transactionId = execution.getProcessBusinessKey();
//        final String getProcessType = "testChronograph";
//        LocalDateTime launchTime =  dateUtil.getNowUTC();
//        launchTime = launchTime.plusMinutes(5L);
//        Task task = new Task(Task.ActionType.SCHEDULE, "12345", "678910", launchTime, "");
//        messageProducer.send(task);
//        final OperationValue operation = new OperationValue(
//                transactionId,
//                1,
//                "produce task for chronograph",
//                "yoda",
//                "chronograph",
//                getProcessType,
//                jsonUtil.toJson(task));
//
//        operationService.saveOperation(operation);
    }
}

