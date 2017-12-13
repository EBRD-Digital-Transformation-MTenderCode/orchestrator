package com.procurement.orchestrator.delegate.fs;

import com.procurement.orchestrator.rest.AccessRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FsAccessPatchEin implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(FsAccessPatchEin.class);

    private final AccessRestClient accessRestClient;

    private final OperationService operationService;

    private final JsonUtil jsonUtil;

    public FsAccessPatchEin(final AccessRestClient accessRestClient,
                            final OperationService operationService,
                            final JsonUtil jsonUtil) {
        this.accessRestClient = accessRestClient;
        this.operationService = operationService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info("->Data preparation for E-Access.");
    }
}
