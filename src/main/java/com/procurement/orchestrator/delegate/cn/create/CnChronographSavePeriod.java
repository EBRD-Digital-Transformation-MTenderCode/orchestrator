package com.procurement.orchestrator.delegate.cn.create;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.kafka.MessageProducer;
import com.procurement.orchestrator.kafka.dto.ChronographTask;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.time.LocalDateTime;
import java.util.Optional;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CnChronographSavePeriod implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CnChronographSavePeriod.class);

    private final MessageProducer messageProducer;

    private final OperationService operationService;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;

    public CnChronographSavePeriod(final MessageProducer messageProducer,
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
        LOG.info(execution.getCurrentActivityName());
        final Optional<OperationStepEntity> entityOptional = operationService.getPreviousOperationStep(execution);
        if (entityOptional.isPresent()) {
            final OperationStepEntity entity = entityOptional.get();
            final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
            ChronographTask.TaskMetaData taskMetaData = new ChronographTask.TaskMetaData(
                    params.getProcessType(),
                    params.getOperationId());
            ChronographTask task = new ChronographTask(
                    ChronographTask.ActionType.SCHEDULE,
                    params.getCpid(),
                    "cn",
                    getPeriodEndDate(entity),
                    jsonUtil.toJson(taskMetaData));
            messageProducer.sendToChronograph(task);
        }
    }

    private LocalDateTime getPeriodEndDate(OperationStepEntity entity) {
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
        final JsonNode tenderNode = jsonData.get("tender");
        final JsonNode tenderPeriodNode = tenderNode.get("tenderPeriod");
        return dateUtil.stringToLocal(tenderPeriodNode.get("endDate").asText());
    }
}
