package com.procurement.orchestrator.delegate.tender.qualification;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.cassandra.model.OperationStepEntity;
import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.rest.QualificationRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Objects;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QualificationUpdateAward implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(QualificationUpdateAward.class);

    private final QualificationRestClient qualificationRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public QualificationUpdateAward(final QualificationRestClient qualificationRestClient,
                                    final OperationService operationService,
                                    final ProcessService processService,
                                    final JsonUtil jsonUtil) {
        this.qualificationRestClient = qualificationRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
        final String processId = execution.getProcessInstanceId();
        final String operationId = params.getOperationId();
        try {
            final JsonNode responseData = processService.processResponse(
                    qualificationRestClient.updateAward(
                            params.getCpid(),
                            params.getStage(),
                            params.getToken(),
                            params.getOwner(),
                            jsonData),
                    processId,
                    operationId);
            if (Objects.nonNull(responseData))
                operationService.saveOperationStep(execution, entity, params, responseData);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            processService.processException(e.getMessage(), processId);
        }
    }
}
