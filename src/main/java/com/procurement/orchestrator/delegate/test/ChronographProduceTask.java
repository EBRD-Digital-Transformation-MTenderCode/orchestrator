package com.procurement.orchestrator.delegate.test;

import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.kafka.MessageProducer;
import com.procurement.orchestrator.kafka.dto.ChronographTask;
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
        final Optional<OperationStepEntity> entityOptional = operationService.getPreviousOperationStep(execution);
        if (entityOptional.isPresent()) {
            final OperationStepEntity entity = entityOptional.get();
            final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
            ChronographTask.TaskMetaData taskMetaData = new ChronographTask.TaskMetaData("consumeTask", "123");
            ChronographTask task = new ChronographTask(
                    ChronographTask.ActionType.SCHEDULE,
                    "123",
                    "test",
                    dateUtil.localDateTimeNowUTC().plusMinutes(5L),
                    jsonUtil.toJson(taskMetaData));
            messageProducer.sendToChronograph(task);
        }
    }
}

