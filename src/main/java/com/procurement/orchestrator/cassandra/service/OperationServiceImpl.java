package com.procurement.orchestrator.cassandra.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.dao.CassandraDao;
import com.procurement.orchestrator.cassandra.model.OperationEntity;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.model.RequestEntity;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.exception.OperationException;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Optional;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service
public class OperationServiceImpl implements OperationService {

    private final static String LAST_TASK = "lastExecutedTask";

    private final CassandraDao cassandraDao;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;

    public OperationServiceImpl(final CassandraDao cassandraDao,
                                final ProcessService processService,
                                final JsonUtil jsonUtil,
                                final DateUtil dateUtil) {
        this.cassandraDao = cassandraDao;
        this.processService = processService;
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
    public OperationStepEntity getOperationStep(final String processId, final String taskId) {
        Optional<OperationStepEntity> entityOptional = cassandraDao.getOperationStep(processId, taskId);
        if (entityOptional.isPresent()) {
            return entityOptional.get();
        } else {
            processService.processException("No data found for taskId :" + taskId, processId);
            return null;
        }
    }

    @Override
    public OperationStepEntity getPreviousOperationStep(final DelegateExecution execution) {
        final String processId = execution.getProcessInstanceId();
        final String taskId = (String) execution.getVariable(LAST_TASK);
        return getOperationStep(processId, taskId);
    }

    @Override
    public void saveOperationStep(final DelegateExecution execution,
                                  final OperationStepEntity entity,
                                  final Params params,
                                  final JsonNode response
    ) {
        execution.setVariable(LAST_TASK, execution.getCurrentActivityId());
        entity.setTaskId(execution.getCurrentActivityId());
        entity.setJsonParams(jsonUtil.toJson(params));
        entity.setJsonData(jsonUtil.toJson(response));
        entity.setDate(dateUtil.getNowUTC());
        cassandraDao.saveOperationStep(entity);
    }

    @Override
    public void saveOperationStep(DelegateExecution execution, OperationStepEntity entity, Params params) {
        execution.setVariable(LAST_TASK, execution.getCurrentActivityId());
        entity.setTaskId(execution.getCurrentActivityId());
        entity.setJsonParams(jsonUtil.toJson(params));
        entity.setDate(dateUtil.getNowUTC());
        cassandraDao.saveOperationStep(entity);
    }

    @Override
    public void saveOperationStep(final DelegateExecution execution,
                                  final OperationStepEntity entity,
                                  final JsonNode response) {
        execution.setVariable(LAST_TASK, execution.getCurrentActivityId());
        entity.setTaskId(execution.getCurrentActivityId());
        entity.setJsonData(jsonUtil.toJson(response));
        entity.setDate(dateUtil.getNowUTC());
        cassandraDao.saveOperationStep(entity);
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

