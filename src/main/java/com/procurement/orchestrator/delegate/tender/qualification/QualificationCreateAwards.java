package com.procurement.orchestrator.delegate.tender.qualification;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
public class QualificationCreateAwards implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(QualificationCreateAwards.class);

    private final QualificationRestClient qualificationRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public QualificationCreateAwards(final QualificationRestClient qualificationRestClient,
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
        final String processId = execution.getProcessInstanceId();
        final OperationStepEntity bids = operationService.getOperationStep(processId, "SubmissionGetBidsTask");
        final OperationStepEntity lots = operationService.getOperationStep(processId, "AccessGetLotsTask");
        final Params params = jsonUtil.toObject(Params.class, lots.getJsonParams());
        final String operationId = params.getOperationId();
        final JsonNode jsonData = prepareData(bids, lots, processId);
        final String taskId = execution.getCurrentActivityId();
        final JsonNode responseData = processService.processResponse(
                qualificationRestClient.createAwards(
                        params.getCpid(),
                        params.getStage(),
                        params.getOwner(),
                        params.getCountry(),
                        params.getPmd(),
                        params.getStartDate(),
                        jsonData),
                processId,
                operationId,
                taskId);
        if (Objects.nonNull(responseData))
            operationService.saveOperationStep(execution, bids, responseData);
    }

    private JsonNode prepareData(OperationStepEntity bids,
                                 OperationStepEntity lots,
                                 final String processId) {

        try {
            final JsonNode jsonData = jsonUtil.toJsonNode(bids.getJsonData());
            final JsonNode lotsJsonData = jsonUtil.toJsonNode(lots.getJsonData());
            ((ObjectNode) jsonData).put("lots", lotsJsonData.get("lots").asText());
            return jsonData;
        } catch (Exception e) {
            processService.terminateProcess(processId, e.getMessage());
            return null;
        }
    }

}


