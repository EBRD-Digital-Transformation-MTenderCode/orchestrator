package com.procurement.orchestrator.cassandra.service;

import com.procurement.orchestrator.cassandra.dao.CassandraDao;
import com.procurement.orchestrator.cassandra.model.OperationEntity;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.model.RequestEntity;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.exception.OperationException;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Objects;
import java.util.Optional;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service
public class OperationServiceImpl implements OperationService {

    private final static String LAST_TASK = "lastExecutedTask";

    private final CassandraDao cassandraDao;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;

    public OperationServiceImpl(final CassandraDao cassandraDao,
                                final JsonUtil jsonUtil,
                                final DateUtil dateUtil) {
        this.cassandraDao = cassandraDao;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
    }

    @Override
    public void checkOperationById(final String operationId) {
        if (cassandraDao.isOperationExist(operationId)) {
            throw new OperationException("Operation with current id already exist.");
        }
    }

    @Override
    public Boolean saveIfNotExist(final String operationId,
                                  final String processId) {
        OperationEntity operationEntity = new OperationEntity();
        operationEntity.setOperationId(operationId);
        operationEntity.setProcessId(processId);
        return cassandraDao.saveOperationIfNotExist(operationEntity);
    }

    @Override
    public void saveFirstOperationStep(final DelegateExecution execution, final RequestEntity requestEntity) {
        execution.setVariable(LAST_TASK, execution.getCurrentActivityId());
        final OperationStepEntity operationStepEntity = new OperationStepEntity();
        operationStepEntity.setProcessId(execution.getProcessInstanceId());
        operationStepEntity.setTaskId(execution.getCurrentActivityId());
        operationStepEntity.setDate(dateUtil.getNowUTC());
        operationStepEntity.setJsonParams(requestEntity.getJsonParams());
        operationStepEntity.setJsonData(requestEntity.getJsonData());
        cassandraDao.saveOperationStep(operationStepEntity);
    }

    @Override
    public Optional<OperationStepEntity> getOperationStep(final String processId, final String taskId) {
        return cassandraDao.getOperationStep(processId, taskId);
    }

    @Override
    public Optional<OperationStepEntity> getPreviousOperationStep(final DelegateExecution execution) {
        return cassandraDao.getOperationStep(
                execution.getProcessInstanceId(),
                (String) execution.getVariable(LAST_TASK)
        );
    }

    @Override
    public void saveOperationStep(final DelegateExecution execution,
                                  final OperationStepEntity entity,
                                  final Params params,
                                  final Object response
    ) {
        if (Objects.nonNull(response)) {
            execution.setVariable(LAST_TASK, execution.getCurrentActivityId());
            entity.setTaskId(execution.getCurrentActivityId());
            entity.setJsonParams(jsonUtil.toJson(params));
            entity.setJsonData(jsonUtil.toJson(jsonUtil.toJsonNode(response)));
            entity.setDate(dateUtil.getNowUTC());
            cassandraDao.saveOperationStep(entity);
        }
    }

    @Override
    public void saveOperationStep(final DelegateExecution execution,
                                  final OperationStepEntity entity,
                                  final Object response) {
        if (Objects.nonNull(response)) {
            execution.setVariable(LAST_TASK, execution.getCurrentActivityId());
            entity.setTaskId(execution.getCurrentActivityId());
            entity.setJsonData(jsonUtil.toJson(jsonUtil.toJsonNode(response)));
            entity.setDate(dateUtil.getNowUTC());
            cassandraDao.saveOperationStep(entity);
        }
    }

    @Override
    public void saveOperationStep(final DelegateExecution execution,
                                  final OperationStepEntity entity) {
        execution.setVariable(LAST_TASK, execution.getCurrentActivityId());
        entity.setTaskId(execution.getCurrentActivityId());
        entity.setDate(dateUtil.getNowUTC());
        cassandraDao.saveOperationStep(entity);
    }
}

