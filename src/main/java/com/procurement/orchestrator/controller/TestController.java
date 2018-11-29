package com.procurement.orchestrator.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.rest.RegulationRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static com.procurement.orchestrator.domain.commands.RegulationCommandType.GET_TERMS;

@RestController
@RequestMapping("/test")
public class TestController {


//    private final RegulationRestClient regulationRestClient;



    private final OperationService operationService;
    private final ProcessService processService;
    private final JsonUtil jsonUtil;

    public TestController(final OperationService operationService,
                          final ProcessService processService,
                          final JsonUtil jsonUtil) {
        this.operationService = operationService;
        this.processService = processService;
        this.jsonUtil = jsonUtil;
    }


//    @RequestMapping(value = "/regulation/{processId}/{lastTaskId}", method = RequestMethod.POST)
//    public ResponseEntity<String> regulation(@PathVariable("processId") final String processId,
//                                             @PathVariable("lastTaskId") final String lastTaskId) {

//        final OperationStepEntity entity = operationService.getOperationStep(processId, lastTaskId);
//        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
//        final JsonNode jsonData = jsonUtil.toJsonNode(entity.getResponseData());
//
//        final JsonNode rqData = processService.getDataForGetTerms(jsonData, processId);
//        if (rqData != null) {
//            final JsonNode commandMessage = processService.getCommandMessage(GET_TERMS, context, jsonData);
//            JsonNode responseData = processService.processResponse(
//                    regulationRestClient.execute(commandMessage),
//                    context,
//                    processId,
//                    "",
//                    commandMessage);
//            if (responseData != null) {
//                execution.setVariable(LAST_TASK, execution.getCurrentActivityId());
//                entity.setTaskId(execution.getCurrentActivityId());
//                entity.setContext(jsonUtil.toJson(context));
//                entity.setRequestData(jsonUtil.toJson(request));
//                entity.setResponseData(jsonUtil.toJson(response));
//                entity.setDate(dateUtil.dateNowUTC());
//                if (Objects.nonNull(context.getCpid()))
//                    entity.setCpId(context.getCpid());
//                cassandraDao.saveOperationStep(entity);
//
//            }
//                operationService.saveOperationStep(
//                        null,
//                        entity,
//                        context,
//                        commandMessage,
//                        processService.addContractTerms(jsonData, responseData, processId));
//            }
//
//
//
//
//
//        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
//    }

}