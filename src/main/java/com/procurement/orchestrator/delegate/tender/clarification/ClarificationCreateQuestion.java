package com.procurement.orchestrator.delegate.tender.clarification;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.delegate.tender.access.AccessCreateCn;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.rest.ClarificationRestClient;
import com.procurement.orchestrator.rest.SubmissionRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Objects;
import java.util.Optional;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ClarificationCreateQuestion implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ClarificationCreateQuestion.class);

    private final ClarificationRestClient clarificationRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;

    public ClarificationCreateQuestion(final ClarificationRestClient clarificationRestClient,
                                       final OperationService operationService,
                                       final ProcessService processService,
                                       final JsonUtil jsonUtil,
                                       final DateUtil dateUtil) {
        this.clarificationRestClient = clarificationRestClient;
        this.operationService = operationService;
        this.processService = processService;
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
            final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
            final String processId = execution.getProcessInstanceId();
            final String operationId = params.getOperationId();
            try {
                final JsonNode responseData = processService.processResponse(
                        clarificationRestClient.createEnquiry(
                                params.getCpid(),
                                params.getStage(),
                                dateUtil.format(dateUtil.localDateTimeNowUTC()),
                                params.getOwner(),
                                jsonData),
                        processId,
                        operationId);
                if (Objects.nonNull(responseData))
                    operationService.saveOperationStep(
                            execution,
                            entity,
                            addDataToParams(params, responseData, processId, operationId),
                            responseData);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                processService.processException(e.getMessage(), processId);
            }
        }
    }

    private Params addDataToParams(final Params params,
                                   final JsonNode responseData,
                                   final String processId,
                                   final String operationId) {
        try {
            params.setToken(responseData.get("token").asText());
            return params;
        } catch (Exception e) {
            processService.processError(e.getMessage(), processId, operationId);
            return null;
        }
    }

}
