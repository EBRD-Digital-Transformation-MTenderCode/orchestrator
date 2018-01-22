package com.procurement.orchestrator.delegate.testChronograph;

import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.kafka.MessageProducer;
import com.procurement.orchestrator.kafka.Task;
import com.procurement.orchestrator.kafka.TaskMetaData;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Optional;
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

    private final DateUtil dateUtil;

    public ChronographProduceTask(final MessageProducer messageProducer,
                                  final OperationService operationService,
                                  final JsonUtil jsonUtil,
                                  final DateUtil dateUtil) {
        this.messageProducer = messageProducer;
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info("->Data preparation for Chronograph.");
        final String sourceTask = (String) execution.getVariableLocal("input_source");
        final String processId = execution.getProcessInstanceId();
        final Optional<OperationStepEntity> entityOptional = operationService.getOperationStep(processId, sourceTask);
        if (entityOptional.isPresent()) {
            final OperationStepEntity entity = entityOptional.get();
            final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
            TaskMetaData taskMetaData = new TaskMetaData("consumeTask", params.getOperationId());
            Task task = new Task(
                    Task.ActionType.SCHEDULE,
                    params.getToken(),
                    "testChronograph",
                    dateUtil.localDateTimeNowUTC().plusMinutes(1L),
                    jsonUtil.toJson(taskMetaData));
            messageProducer.send(task);
        }
    }
}

