package com.procurement.orchestrator.delegate.auction;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class AuctionStart implements JavaDelegate {

//    private static final Logger LOG = LoggerFactory.getLogger(AuctionSchedule.class);
//
//    private final AuctionRestClient auctionRestClient;
//
//    private final OperationService operationService;
//
//    private final ProcessService processService;
//
//    private final JsonUtil jsonUtil;
//
//    public AuctionStart(final AuctionRestClient auctionRestClient,
//                           final OperationService operationService,
//                           final ProcessService processService,
//                           final JsonUtil jsonUtil) {
//        this.auctionRestClient = auctionRestClient;
//        this.operationService = operationService;
//        this.processService = processService;
//        this.jsonUtil = jsonUtil;
//    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
//        LOG.info(execution.getCurrentActivityName());
//        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
//        final JsonNode prevData = jsonUtil.toJsonNode(entity.getResponseData());
//        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
//        final String processId = execution.getProcessInstanceId();
//        final String taskId = execution.getCurrentActivityId();
//        final JsonNode rqData = processService.getAuctionData(prevData, processId);
//        final JsonNode commandMessage = processService.getCommandMessage(START, context, rqData);
//        if (rqData != null) {
//            JsonNode responseData = processService.processResponse(
//                    auctionRestClient.execute(commandMessage),
//                    context,
//                    processId,
//                    taskId,
//                    commandMessage);
//            if (Objects.nonNull(responseData)) {
//                context.setIsAuction(true);
//                operationService.saveOperationStep(
//                        execution,
//                        entity,
//                        context,
//                        commandMessage,
//                        processService.setAuctionData(prevData, responseData, processId));
//            }
//        }
    }
}
