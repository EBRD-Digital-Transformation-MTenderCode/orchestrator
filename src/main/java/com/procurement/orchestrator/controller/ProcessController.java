package com.procurement.orchestrator.controller;

import com.procurement.orchestrator.model.dto.ResponseMessage;
import com.procurement.orchestrator.service.ProcessService;
import com.procurement.orchestrator.service.OperationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/processes")
public class ProcessController {

    private final ProcessService processService;

    private final OperationService operationService;

    public ProcessController(final ProcessService processService,
                             final OperationService operationService) {
        this.processService = processService;
        this.operationService = operationService;
    }

    @RequestMapping(value = "/startProcess", method = RequestMethod.POST)
    public ResponseEntity<String> startProcess(@RequestParam final String operationType,
                                                        @RequestParam final String operationId,
                                                        @RequestBody String jsonData) {
       /**check/save operation data*/
       operationService.processOperation(operationType, operationId, jsonData);
       /**start new process for current operation*/
       processService.startProcessInstanceByKey(operationType, operationId);
       return new ResponseEntity<>(ResponseMessage.OK.value(), HttpStatus.CREATED);
    }

//    @RequestMapping(value = "/receiveTask", method = RequestMethod.GET)
//    public String receiveTask(@RequestParam Integer bpId) {
//        if (bpId == null) {
//            bpId = bp;
//        }
//        ProcessInstance pi = runtimeService.createProcessInstanceQuery()
//                .processInstanceBusinessKey(String.valueOf(bpId))
//                .singleResult();
//        Execution execution = runtimeService.createExecutionQuery()
//                .processInstanceId(pi.getId()).activityId("receiveMessageTask").singleResult();
//        if (execution != null) {
//            runtimeService.signal(execution.getId());
//            System.out.println("");
//            System.out.println("->Receive task ok");
//            System.out.println("");
//            System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date()));
//            System.out.println("->Timer duration: 3 min");
//            return "receive task ok";
//        }
//
//
//        bp = bp + 1;
//        Integer currentRound = 0;
//        Integer numberOfRounds = 1;
//        Integer currentBid = 0;
//        Integer numberOfBids = 1;
//        Boolean areThereAwaitingBidders = false;
//        Boolean priceChangeInThePreviousRound = false;
//        Boolean passedTheFirstTreeRounds = false;
//        Boolean roundTimeExpired = false;
//
//        Map<String, Object> vars = new HashMap<>();
//        vars.put("currentRound", currentRound);
//        vars.put("numberOfRounds", numberOfRounds);
//        vars.put("passedTheFirstTreeRounds", passedTheFirstTreeRounds);
//        vars.put("currentBid", currentBid);
//        vars.put("numberOfBids", numberOfBids);
//        vars.put("areThereAwaitingBidders", areThereAwaitingBidders);
//        vars.put("priceChangeInThePreviousRound", priceChangeInThePreviousRound);
//        vars.put("roundTimeExpired", roundTimeExpired);
//
//        System.out.println("");
//        System.out.println("->Start");
//        System.out.println("");
//
//        runtimeService.startProcessInstanceByKey("Sample", String.valueOf(bp), vars);
//
//
//        return "receive task fail";
//    }
//
//    @RequestMapping(value = "/processMessage", method = RequestMethod.GET)
//    public Long processMessage() {
//        bp = bp + 1;
//        OperationEntity transactionEntity = new OperationEntity("hello", new Date());
//        transactionRepository.save(transactionEntity);
//        Map<String, Object> vars = Collections.singletonMap("transactionEntity", transactionEntity);
//        runtimeService.startProcessInstanceByKey("messageProcess", String.valueOf(bp), vars);
//        return transactionEntity.getId();
//    }
//
//    @RequestMapping(value = "/receiveMessage", method = RequestMethod.GET)
//    public String receiveMessage() {
//        ProcessInstance pi = runtimeService.createProcessInstanceQuery()
//                .processInstanceBusinessKey(String.valueOf(bp))
//                .singleResult();
//        EventSubscription subscription = runtimeService.createEventSubscriptionQuery()
//                .processInstanceId(pi.getId()).eventType("message").singleResult();
//
//        runtimeService.messageEventReceived("bidderMessage", subscription.getExecutionId());
//
//        return "receive task fail";
//    }
//
//    @RequestMapping(value = "/processMessageTest", method = RequestMethod.GET)
//    public String processMessageTest() {
//        runtimeService.startProcessInstanceByKey("messageProcessTest");
//        return "ok";
//    }

//    @RequestMapping(value = "/startProcessByName", method = RequestMethod.POST)
//    public String startProcessByName(@RequestParam String name) {
//        String deploymentID = repositoryService.createDeployment()
//                .enableDuplicateFiltering(true)
//                .addInputStream(resourceName, byteArrayInputStream).deploy().getId();
//        runtimeService.startProcessInstanceByKey(name);
//        return "ok";
//    }

}
