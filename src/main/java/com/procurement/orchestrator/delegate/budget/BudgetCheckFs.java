package com.procurement.orchestrator.delegate.budget;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Params;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.BudgetRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import java.util.Objects;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BudgetCheckFs implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(BudgetCheckFs.class);

    private final BudgetRestClient budgetRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;


    public BudgetCheckFs(final BudgetRestClient budgetRestClient,
                         final OperationService operationService,
                         final ProcessService processService,
                         final JsonUtil jsonUtil) {
        this.budgetRestClient = budgetRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();
        final JsonNode checkFsDto = processService.getCheckFs(jsonData, processId);

        final JsonNode responseData = processService.processResponse(
                budgetRestClient.checkFs(checkFsDto),
                params,
                processId,
                taskId);

        if (Objects.nonNull(responseData))
            operationService.saveOperationStep(
                    execution,
                    entity,
                    processService.setCheckFs(jsonData, responseData, processId));
    }
}
