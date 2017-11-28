package com.procurement.orchestrator.service;


import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class ShowNumberOfRoundsDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println("->Show number of rounds; numberOfRounds: " + execution.getVariableLocal("numberOfRounds"));
    }

}