package com.procurement.orchestrator.delegate.auction;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.AuctionRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static com.procurement.orchestrator.domain.commands.AuctionCommandType.SCHEDULE;


@Component
public class AuctionSchedule implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(AuctionSchedule.class);

    private final AuctionRestClient auctionRestClient;
    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public AuctionSchedule(
            final AuctionRestClient auctionRestClient,
            final OperationService operationService,
            final ProcessService processService,
            final JsonUtil jsonUtil
    ) {
        this.auctionRestClient = auctionRestClient;
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info(execution.getCurrentActivityId());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final String processId = execution.getProcessInstanceId();
        final String taskId = execution.getCurrentActivityId();

        final JsonNode prevData = jsonUtil.toJsonNode(entity.getResponseData());
        final JsonNode rqData = processService.getAuctionData(prevData, processId);
        if (rqData != null) {
            final Context contextForCommand = new Context();
            contextForCommand.setCpid(context.getCpid());
            final String ocidCn = context.getOcidCn();
            if (ocidCn != null)
                contextForCommand.setOcid(ocidCn);
            else
                contextForCommand.setOcid(context.getOcid());
            contextForCommand.setOperationId(context.getOperationId());
            contextForCommand.setStartDate(context.getStartDate());
            contextForCommand.setPmd(context.getPmd());
            contextForCommand.setCountry(context.getCountry());

            final JsonNode commandMessage = processService.getCommandMessage(SCHEDULE, contextForCommand, rqData);
            if (LOG.isDebugEnabled())
                LOG.debug("COMMAND ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(commandMessage));

            final ResponseEntity<ResponseDto> response = auctionRestClient.execute(commandMessage);
            if (LOG.isDebugEnabled())
                LOG.debug("RESPONSE FROM SERVICE ({}): '{}'.", context.getOperationId(), jsonUtil.toJson(response.getBody()));

            final JsonNode responseData = processService.processResponse(response, context, processId, taskId, commandMessage);
            if (LOG.isDebugEnabled())
                LOG.debug("RESPONSE AFTER PROCESSING ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(responseData));

            if (responseData != null) {
                context.setIsAuction(true);
                if (LOG.isDebugEnabled())
                    LOG.debug("COMMAND ({}) IN CONTEXT CHANGED THE IS AUCTION TO '{}'.", context.getOperationId(), context.getIsAuction());

                final JsonNode step = processService.setAuctionData(prevData, responseData, processId);
                if (LOG.isDebugEnabled())
                    LOG.debug("STEP FOR SAVE ({}): '{}'.", context.getOperationId(), jsonUtil.toJsonOrEmpty(step));

                operationService.saveOperationStep(execution, entity, context, commandMessage, step);
            }
        }
    }
}
