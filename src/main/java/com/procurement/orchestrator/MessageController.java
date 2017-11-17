package com.procurement.orchestrator;

import com.procurement.orchestrator.model.Message;
import com.procurement.orchestrator.model.MessageRepository;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/processes")
public class MessageController {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private MessageRepository messageRepository;

    private int bp = 0;

    @RequestMapping(value = "/testProcess", method = RequestMethod.GET)
    public String testProcess() {
        bp = bp + 1;
        Integer currentRound = 0;
        Integer numberOfRounds = 1;
        Integer currentBid = 0;
        Integer numberOfBids = 1;
        Boolean areThereAwaitingBidders = false;
        Boolean priceChangeInThePreviousRound = false;
        Boolean passedTheFirstTreeRounds = false;
        Boolean roundTimeExpired = false;

        Map<String, Object> vars = new HashMap<>();
        vars.put("currentRound", currentRound);
        vars.put("numberOfRounds", numberOfRounds);
        vars.put("passedTheFirstTreeRounds", passedTheFirstTreeRounds);
        vars.put("currentBid", currentBid);
        vars.put("numberOfBids", numberOfBids);
        vars.put("areThereAwaitingBidders", areThereAwaitingBidders);
        vars.put("priceChangeInThePreviousRound", priceChangeInThePreviousRound);
        vars.put("roundTimeExpired", roundTimeExpired);

        System.out.println("");
        System.out.println("->Start");
        System.out.println("");

        runtimeService.startProcessInstanceByKey("Sample", String.valueOf(bp), vars);
        return "start process";
    }

    @RequestMapping(value = "/receiveTask", method = RequestMethod.GET)
    public String receiveTask(@RequestParam Integer bpId) {
        if (bpId == null) {
            bpId = bp;
        }
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(String.valueOf(bpId))
                .singleResult();
        Execution execution = runtimeService.createExecutionQuery()
                .processInstanceId(pi.getId()).activityId("receiveMessageTask").singleResult();
        if (execution != null) {
            runtimeService.signal(execution.getId());
            System.out.println("");
            System.out.println("->Receive task ok");
            System.out.println("");
            System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date()));
            System.out.println("->Timer duration: 3 min");
            return "receive task ok";
        }
        return "receive task fail";
    }

    @RequestMapping(value = "/processMessage", method = RequestMethod.GET)
    public Long processMessage() {
        bp = bp + 1;
        Message message = new Message("hello", new Date());
        messageRepository.save(message);
        Map<String, Object> vars = Collections.singletonMap("message", message);
        runtimeService.startProcessInstanceByKey("messageProcess", String.valueOf(bp), vars);
        return message.getId();
    }

    @RequestMapping(value = "/receiveMessage", method = RequestMethod.GET)
    public String receiveMessage() {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(String.valueOf(bp))
                .singleResult();
        EventSubscription subscription = runtimeService.createEventSubscriptionQuery()
                .processInstanceId(pi.getId()).eventType("message").singleResult();

        runtimeService.messageEventReceived("bidderMessage", subscription.getExecutionId());

        return "receive task fail";
    }

    @RequestMapping(value = "/processMessageTest", method = RequestMethod.GET)
    public String processMessageTest() {
        runtimeService.startProcessInstanceByKey("messageProcessTest");
        return "ok";
    }

//    @RequestMapping(value = "/startProcessByName", method = RequestMethod.POST)
//    public String startProcessByName(@RequestParam String name) {
//        String deploymentID = repositoryService.createDeployment()
//                .enableDuplicateFiltering(true)
//                .addInputStream(resourceName, byteArrayInputStream).deploy().getId();
//        runtimeService.startProcessInstanceByKey(name);
//        return "ok";
//    }

}
