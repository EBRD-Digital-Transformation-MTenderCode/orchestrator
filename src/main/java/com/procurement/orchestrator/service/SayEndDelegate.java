package com.procurement.orchestrator.service;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class SayEndDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println("");
        System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date()));
        System.out.println("->End");
        System.out.println("");
    }
}