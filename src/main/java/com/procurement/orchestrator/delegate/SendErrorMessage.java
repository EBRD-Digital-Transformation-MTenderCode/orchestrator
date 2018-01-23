package com.procurement.orchestrator.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class SendErrorMessage implements JavaDelegate {

    @Override
    public void execute(final DelegateExecution execution) {
        System.out.println("");
        System.out.println("->Send error to Platform.");
        System.out.println("");
    }
}
