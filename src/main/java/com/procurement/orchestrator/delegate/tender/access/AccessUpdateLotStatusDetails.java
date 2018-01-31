package com.procurement.orchestrator.delegate.tender.access;

import com.procurement.orchestrator.cassandra.service.OperationService;
import com.procurement.orchestrator.rest.AccessRestClient;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AccessUpdateLotStatusDetails implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(AccessUpdateLotStatusDetails.class);

    private final AccessRestClient accessRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public AccessUpdateLotStatusDetails(final AccessRestClient accessRestClient,
                                        final OperationService operationService,
                                        final ProcessService processService,
                                        final JsonUtil jsonUtil) {
        this.accessRestClient = accessRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityName());
//        final Optional<OperationStepEntity> entityOptional = operationService.getPreviousOperationStep(execution);
//        if (entityOptional.isPresent()) {
//            final OperationStepEntity entity = entityOptional.get();
//            final Params params = jsonUtil.toObject(Params.class, entity.getJsonParams());
//            final JsonNode jsonData = jsonUtil.toJsonNode(entity.getJsonData());
//            final String processId = execution.getProcessInstanceId();
//            final String operationId = params.getOperationId();
//            try {
//                final JsonNode responseData = processService.processResponse(
//                        accessRestClient.createCn(params.getOwner(), jsonData),
//                        processId,
//                        operationId);
        //                if (Objects.nonNull(responseData))
//                operationService.saveOperationStep(
//                        execution,
//                        entity,
//                        addDataToParams(params, responseData, processId, operationId),
//                        responseData);
//            } catch (Exception e) {
//                LOG.error(e.getMessage(), e);
//                processService.processException(e.getMessage(), processId);
//            }
//        }
    }
}
