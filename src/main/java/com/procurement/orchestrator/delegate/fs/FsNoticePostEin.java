package com.procurement.orchestrator.delegate.fs;

import com.procurement.orchestrator.rest.NoticeRestClient;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FsNoticePostEin implements JavaDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(FsNoticePostEin.class);

    private final OperationService operationService;

    private final NoticeRestClient noticeRestClient;

    private final JsonUtil jsonUtil;

    public FsNoticePostEin(final OperationService operationService,
                           final NoticeRestClient noticeRestClient,
                           final JsonUtil jsonUtil) {
        this.operationService = operationService;
        this.noticeRestClient = noticeRestClient;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info("->Data preparation for E-Notice.");
    }
}
