package com.procurement.orchestrator.delegate.ein;

import com.procurement.orchestrator.service.RestService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class SendDataToAccess implements JavaDelegate {

    private final RestService restService;

    public SendDataToAccess(final RestService restService) {
        this.restService = restService;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        System.out.println("");
        System.out.println("->Send data to E-Access.");
        System.out.println("");
    }
}
