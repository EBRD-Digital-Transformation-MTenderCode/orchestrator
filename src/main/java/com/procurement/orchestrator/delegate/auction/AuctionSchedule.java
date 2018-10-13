package com.procurement.orchestrator.delegate.auction;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.AuctionRestClient;
import com.procurement.orchestrator.rest.MdmRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.procurement.orchestrator.domain.commands.AuctionCommandType.SCHEDULE;

@Component
public class AuctionSchedule implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(AuctionSchedule.class);

    private final AuctionRestClient auctionRestClient;

    private final OperationService operationService;

    private final ProcessService processService;

    private final JsonUtil jsonUtil;

    public AuctionSchedule(final AuctionRestClient auctionRestClient,
                           final OperationService operationService,
                           final ProcessService processService,
                           final JsonUtil jsonUtil) {
        this.auctionRestClient = auctionRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityName());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final JsonNode prevData = jsonUtil.toJsonNode(entity.getResponseData());
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();
        final JsonNode rqData = processService.getAuctionData(prevData, processId);
        final JsonNode commandMessage = processService.getCommandMessage(SCHEDULE, context, rqData);
        if (rqData != null) {
            JsonNode responseData = processService.processResponse(
                    auctionRestClient.execute(commandMessage),
                    context,
                    processId,
                    taskId,
                    commandMessage);
            if (Objects.nonNull(responseData)) {
                operationService.saveOperationStep(
                        execution,
                        entity,
                        commandMessage,
                        processService.setAuctionData(prevData, responseData, processId));
            }
        }
    }
}
