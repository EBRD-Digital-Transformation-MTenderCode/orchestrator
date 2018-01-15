package com.procurement.orchestrator.cassandra.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.dao.CassandraDao;
import com.procurement.orchestrator.cassandra.model.OperationEntity;
import com.procurement.orchestrator.cassandra.model.RequestEntity;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.exception.OperationException;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class OperationServiceImpl implements OperationService {

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
    public void checkOperationByTxId(String txId) {
        if (getLastOperation(txId).isPresent()) {
            throw new OperationException("Operation with current txId already exist.");
        }
    }

    @Override
    public void saveIfNotExist(RequestEntity requestEntity, String processId) {
        checkOperationByTxId(requestEntity.getTxId());
        OperationEntity operationEntity = new OperationEntity();
        operationEntity.setTxId(requestEntity.getTxId());
        operationEntity.setDate(dateUtil.getNowUTC());
        operationEntity.setProcessId(processId);
        operationEntity.setJsonData(requestEntity.getJsonData());
        operationEntity.setJsonParams(requestEntity.getJsonParams());
        operationEntity.setRequestDate(requestEntity.getRequestDate());
        cassandraDao.saveOperation(operationEntity);
    }

    @Override
    public void saveOperation(final OperationEntity entity) throws OperationException {
        cassandraDao.saveOperation(entity);
    }

    @Override
    public Optional<OperationEntity> getLastOperation(final String txId) {
        return cassandraDao.getLastOperation(txId);
    }

    @Override
    public void processResponse(OperationEntity entity, Params params, Object response) {
        final JsonNode jsonData = jsonUtil.toJsonNode(response);
        params.setToken(jsonData.get("token").asText());
        entity.setJsonParams(jsonUtil.toJson(params));
        entity.setJsonData(jsonUtil.toJson(jsonData));
        entity.setDate(dateUtil.getNowUTC());
        cassandraDao.saveOperation(entity);
    }

    @Override
    public void processResponse(OperationEntity entity, Object response) {
        final JsonNode jsonData = jsonUtil.toJsonNode(response);
        entity.setJsonData(jsonUtil.toJson(jsonData));
        entity.setDate(dateUtil.getNowUTC());
        cassandraDao.saveOperation(entity);
    }

    @Override
    public void processResponse(OperationEntity entity) {
        entity.setDate(dateUtil.getNowUTC());
        cassandraDao.saveOperation(entity);
    }
}

