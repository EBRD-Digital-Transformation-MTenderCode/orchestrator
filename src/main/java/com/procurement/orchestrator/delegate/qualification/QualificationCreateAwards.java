package com.procurement.orchestrator.delegate.qualification;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.QualificationRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Objects;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QualificationCreateAwards implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(QualificationCreateAwards.class);

    private final QualificationRestClient qualificationRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;
    private final DateUtil dateUtil;

    public QualificationCreateAwards(final QualificationRestClient qualificationRestClient,
                                     final OperationService operationService,
                                     final ProcessService processService,
                                     final JsonUtil jsonUtil,
                                     final DateUtil dateUtil) {
        this.qualificationRestClient = qualificationRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityName());
        final String processId = execution.getProcessInstanceId();
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
        final JsonNode requestData = jsonUtil.toJsonNode(entity.getResponseData());
        final String taskId = execution.getCurrentActivityId();
        final JsonNode responseData = processService.processResponse(
                qualificationRestClient.createAwards(
                        params.getCpid(),
                        params.getNewStage(),
                        params.getOwner(),
                        params.getCountry(),
                        params.getPmd(),
                        params.getStartDate(),
                        requestData),
                params,
                processId,
                taskId,
                requestData);
        if (Objects.nonNull(responseData)) {
            processParams(execution, params, responseData, processId);
            operationService.saveOperationStep(
                    execution,
                    entity,
                    processService.addAwardAccessToParams(params, responseData, processId),
                    requestData,
                    processService.addAwardData(requestData, responseData, processId));
        }
    }

    private void processParams(final DelegateExecution execution, final Params params, final JsonNode responseData, final String processId) {
        final Boolean isAwardsEmpty = processService.isAwardsEmpty(responseData, processId);
        if (isAwardsEmpty) {
            execution.setVariable("tenderUnsuccessful", 1);
            params.setOperationType("tenderUnsuccessful");
        } else {
            execution.setVariable("tenderUnsuccessful", 0);
        }
    }
}


