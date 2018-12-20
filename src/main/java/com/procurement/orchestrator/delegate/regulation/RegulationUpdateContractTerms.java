package com.procurement.orchestrator.delegate.regulation;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.RegulationRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.procurement.orchestrator.domain.commands.RegulationCommandType.UPDATE_TERMS;

@Component
public class RegulationUpdateContractTerms implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(RegulationUpdateContractTerms.class);

    private final RegulationRestClient regulationRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public RegulationUpdateContractTerms(final RegulationRestClient regulationRestClient,
                                         final OperationService operationService,
                                         final ProcessService processService,
                                         final JsonUtil jsonUtil) {
        this.regulationRestClient = regulationRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityId());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();
        final JsonNode rqData = processService.getAgreedMetrics(jsonData, processId);
        if (rqData != null) {
            final JsonNode commandMessage = processService.getCommandMessage(UPDATE_TERMS, context, rqData);
            JsonNode responseData = processService.processResponse(
                    regulationRestClient.execute(commandMessage),
                    context,
                    processId,
                    taskId,
                    commandMessage);
            if (responseData != null) {
                operationService.saveOperationStep(
                        execution,
                        entity,
                        context,
                        commandMessage,
                        processService.setAgreedMetrics(jsonData, responseData, processId));
            }
        }
    }
}

