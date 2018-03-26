package com.procurement.orchestrator.delegate.qualification;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.service.OperationService;
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
public class QualificationGetAwards implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(QualificationGetAwards.class);

    private final QualificationRestClient qualificationRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public QualificationGetAwards(final QualificationRestClient qualificationRestClient,
                                  final OperationService operationService,
                                  final ProcessService processService,
                                  final JsonUtil jsonUtil) {
        this.qualificationRestClient = qualificationRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();
        params.setOperationType("awardPeriodEnd");
        final JsonNode responseData = processService.processResponse(
                qualificationRestClient.getAwards(
                        params.getCpid(),
                        params.getNewStage(),
                        params.getCountry(),
                        params.getPmd()),
                params,
                processId,
                taskId);
        if (Objects.nonNull(responseData))
            operationService.saveOperationStep(execution, entity, params, responseData);
    }
}
