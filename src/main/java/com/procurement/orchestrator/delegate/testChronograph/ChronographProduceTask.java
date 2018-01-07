package com.procurement.orchestrator.delegate.testChronograph;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

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

