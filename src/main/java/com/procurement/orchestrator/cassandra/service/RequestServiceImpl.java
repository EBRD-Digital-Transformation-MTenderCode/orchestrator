package com.procurement.orchestrator.cassandra.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.dao.CassandraDao;
import com.procurement.orchestrator.cassandra.dao.CassandraDaoImpl;
import com.procurement.orchestrator.cassandra.model.RequestEntity;
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
    public void saveRequest(String txId, Params jsonParams, JsonNode jsonData) {
        cassandraDao.saveRequest(getRequestEntity(txId, jsonParams, jsonData));
    }

    @Override
    public Optional<RequestEntity> getRequest(String txId) {
        return cassandraDao.getOneByTxId(txId);
    }

    private RequestEntity getRequestEntity(final String txId,
                                           final Params jsonParams,
                                           final JsonNode jsonData) {
        RequestEntity entity = new RequestEntity();
        entity.setRequestDate(dateUtil.getNowUTC());
        entity.setTxId(txId);
        entity.setJsonParams(jsonUtil.toJson(jsonParams));
        entity.setJsonData(jsonUtil.toJson(jsonData));
        return entity;
    }
}
