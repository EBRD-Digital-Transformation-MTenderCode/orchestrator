package com.procurement.orchestrator.service;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.dao.CassandraDao;
import com.procurement.orchestrator.dao.CassandraDaoImpl;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Rule;
import com.procurement.orchestrator.domain.Stage;
import com.procurement.orchestrator.domain.TypeOfProcess;
import com.procurement.orchestrator.domain.entity.ContextEntity;
import com.procurement.orchestrator.domain.entity.RequestEntity;
import com.procurement.orchestrator.exception.OperationException;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class RequestServiceImpl implements RequestService {

    private final JsonUtil jsonUtil;
    private final DateUtil dateUtil;
    private final CassandraDao cassandraDao;
    private final ProcessService processService;
    private final OperationService operationService;
    private final String lang = "ro";
    private final String initiator = "platform";


    public RequestServiceImpl(final JsonUtil jsonUtil,
                              final DateUtil dateUtil,
                              final CassandraDaoImpl cassandraDao,
                              final ProcessService processService,
                              final OperationService operationService) {
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
        this.cassandraDao = cassandraDao;
        this.processService = processService;
        this.operationService = operationService;
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

    @Override
    public void saveRequestAndCheckOperation(final Context context, final JsonNode jsonData) {
        final JsonNode data;
        if (Objects.isNull(jsonData)) {
            data = jsonUtil.createObjectNode();
        } else {
            data = jsonData;
        }
        saveRequest(context.getRequestId(), context.getOperationId(), context, data);
        operationService.checkOperationById(context.getOperationId());
    }

    @Override
    public String getOwner(final String authorization) {
        final String[] split = authorization.split("\\.");
        final String payload = split[1];
        final String encodedToken = StringUtils.newStringUtf8(Base64.decodeBase64(payload.getBytes()));
        final JsonNode jsonNode = jsonUtil.toJsonNode(encodedToken);
        return jsonNode.get("idPlatform").asText();
    }

    @Override
    public Context getContext(final String cpId) {
        final Optional<ContextEntity> entityOptional = cassandraDao.getContextByCpId(cpId);
        if (!entityOptional.isPresent()) throw new OperationException("Context not found.");
        return jsonUtil.toObject(Context.class, entityOptional.get().getContext());
    }


    @Override
    public Rule checkAndGetRule(final Context prevContext, final String processType) {
        final List<Rule> rules = cassandraDao.getRules(prevContext.getCountry(), prevContext.getPmd(), processType);
        if (rules.isEmpty()) {
            throw new OperationException("Operation impossible.");
        }
        Rule rule = null;
        for (final Rule r : rules) {
            if (prevContext.getPhase().equals(r.getPrevPhase()) && prevContext.getStage().equals(r.getPrevStage())) {
                rule = r;
            }
        }
        if (rule != null) {
            return rule;
        } else {
            throw new OperationException("Operation impossible.");
        }
    }

    @Override
    public String getProcessType(final String country, final String pmd, final String process) {
        final Optional<TypeOfProcess> entityOptional = cassandraDao.getProcess(country, pmd, process);
        if (!entityOptional.isPresent()) throw new OperationException("Operation impossible.");
        return entityOptional.get().getProcessType();
    }

    @Override
    public Rule getRule(final String country, final String pmd, final String processType) {
        final Optional<Rule> entityOptional = cassandraDao.getRule(country, pmd, processType);
        if (!entityOptional.isPresent()) throw new OperationException("Operation impossible.");
        return entityOptional.get();
    }

    @Override
    public Context getContextForCreate(final String authorization,
                                       final String operationId,
                                       final String country,
                                       final String pmd,
                                       final String process) {
        final Context context = new Context();
        final String processType = getProcessType(country, pmd, process);
        final Rule rules = getRule(country, pmd, processType);

        context.setCountry(rules.getCountry());
        context.setPmd(rules.getPmd());
        context.setProcessType(processType);
        context.setStage(rules.getNewStage());
        context.setPhase(rules.getNewPhase());
        context.setOperationType(rules.getOperationType());

        context.setRequestId(UUIDs.timeBased().toString());
        context.setOwner(getOwner(authorization));
        context.setOperationId(operationId);
        context.setLanguage(lang);
        context.setStartDate(dateUtil.nowFormatted());
        return context;
    }

    @Override
    public Context getContextForUpdate(final String authorization,
                                       final String operationId,
                                       final String cpid,
                                       final String ocid,
                                       final String token,
                                       final String process) {
        final Context prevContext = getContext(cpid);
        if (ocid != null) validateStageFromOcId(cpid, ocid, prevContext);
        final String processType = getProcessType(prevContext.getCountry(), prevContext.getPmd(), process);
        final Rule rule = checkAndGetRule(prevContext, processType);
        final Context context = new Context();
        context.setCountry(rule.getCountry());
        context.setPmd(rule.getPmd());
        context.setProcessType(processType);
        context.setPrevStage(rule.getPrevStage());
        context.setStage(rule.getNewStage());
        context.setPhase(rule.getNewPhase());
        context.setOperationType(rule.getOperationType());

        context.setRequestId(UUIDs.timeBased().toString());
        context.setOperationId(operationId);
        context.setOwner(getOwner(authorization));
        context.setCpid(cpid);
        context.setOcid(ocid);
        context.setToken(token);
        context.setLanguage(lang);
        context.setStartDate(dateUtil.nowFormatted());

        return context;
    }

    private void validateStageFromOcId(final String cpid, final String ocid, final Context prevContext) {
        String currentStage = null;
        for (final Stage stage : Stage.values()) {
            if (ocid.contains(stage.value())) {
                currentStage = stage.value();
            }
        }

        final String cpidFromOcid = ocid.substring(0, ocid.indexOf("-" + currentStage + "-"));
        if (!cpid.equals(cpidFromOcid)) {
            throw new OperationException("Invalid ocid.");
        }

        if (currentStage == null) {
            throw new OperationException("Invalid ocid.");
        } else {
            if (!currentStage.equals(prevContext.getStage())) {
                throw new OperationException("Invalid ocid.");
            }
        }
    }

}
