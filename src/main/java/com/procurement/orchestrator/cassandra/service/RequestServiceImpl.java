package com.procurement.orchestrator.cassandra.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.dao.CassandraDao;
import com.procurement.orchestrator.cassandra.dao.CassandraDaoImpl;
import com.procurement.orchestrator.domain.entity.RequestEntity;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class RequestServiceImpl implements RequestService {

    private final JsonUtil jsonUtil;
    private final DateUtil dateUtil;
    private final CassandraDao cassandraDao;


    public RequestServiceImpl(final JsonUtil jsonUtil,
                              final DateUtil dateUtil,
                              final CassandraDaoImpl cassandraDao) {
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
        this.cassandraDao = cassandraDao;
    }

    @Override
    public void saveRequest(final String requestId,
                            final String operationId,
                            final Params jsonParams,
                            final JsonNode jsonData) {
        cassandraDao.saveRequest(getRequestEntity(requestId, operationId, jsonParams, jsonData));
    }

    @Override
    public Optional<RequestEntity> getRequestById(String requestId) {
        return cassandraDao.getRequestById(requestId);
    }

    private RequestEntity getRequestEntity(final String requestId,
                                           final String operationId,
                                           final Params jsonParams,
                                           final JsonNode jsonData) {
        RequestEntity entity = new RequestEntity();
        entity.setRequestId(requestId);
        entity.setRequestDate(dateUtil.getNowUTC());
        entity.setOperationId(operationId);
        entity.setJsonParams(jsonUtil.toJson(jsonParams));
        entity.setJsonData(jsonUtil.toJson(jsonData));
        return entity;
    }
}
