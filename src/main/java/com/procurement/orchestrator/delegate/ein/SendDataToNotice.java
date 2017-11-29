package com.procurement.orchestrator.delegate.ein;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class SendDataToNotice implements JavaDelegate {

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        System.out.println("");
        System.out.println("->Send data to E-Notice.");
        System.out.println("");
    }
}
