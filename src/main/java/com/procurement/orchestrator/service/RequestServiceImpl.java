package com.procurement.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.dao.CassandraDao;
import com.procurement.orchestrator.dao.CassandraDaoImpl;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.RequestEntity;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RequestServiceImpl implements RequestService {

    private final JsonUtil jsonUtil;
    private final DateUtil dateUtil;
    private final CassandraDao cassandraDao;
    private final ProcessService processService;


    public RequestServiceImpl(final JsonUtil jsonUtil,
                              final DateUtil dateUtil,
                              final CassandraDaoImpl cassandraDao,
                              final ProcessService processService) {
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
        this.cassandraDao = cassandraDao;
        this.processService = processService;
    }

    @Override
    public void saveRequest(final String requestId,
                            final String operationId,
                            final Context context,
                            final JsonNode jsonData) {
        cassandraDao.saveRequest(getRequestEntity(requestId, operationId, context, jsonData));
    }

    @Override
    public RequestEntity getRequestById(final String requestId, final String processId) {
        final Optional<RequestEntity> requestOptional = cassandraDao.getRequestById(requestId);
        if (requestOptional.isPresent()) {
            return requestOptional.get();
        } else {
            processService.terminateProcess(processId, "requestId: " + requestId + " not found.");
            return null;
        }
    }

    private RequestEntity getRequestEntity(final String requestId,
                                           final String operationId,
                                           final Context context,
                                           final JsonNode jsonData) {
        final RequestEntity entity = new RequestEntity();
        entity.setRequestId(requestId);
        entity.setRequestDate(dateUtil.dateNowUTC());
        entity.setOperationId(operationId);
        entity.setContext(jsonUtil.toJson(context));
        entity.setJsonData(jsonUtil.toJson(jsonData));
        return entity;
    }
}
