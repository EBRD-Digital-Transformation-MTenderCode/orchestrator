package com.procurement.orchestrator.service;

import com.procurement.orchestrator.model.entity.Message;
import com.procurement.orchestrator.repository.MessageRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class SaveMessageDelegate implements JavaDelegate {

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Message message = new Message("hello", new Date());
//        messageRepository.save(message);
        System.out.println("");
        System.out.println("->Save message");
        System.out.println("");
    }
}