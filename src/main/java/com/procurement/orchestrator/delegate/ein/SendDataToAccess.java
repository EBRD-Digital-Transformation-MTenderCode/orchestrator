package com.procurement.orchestrator.delegate.ein;

import com.procurement.orchestrator.domain.constant.TargetUrl;
import com.procurement.orchestrator.domain.dto.RequestDto;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import com.procurement.orchestrator.service.RestService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SendDataToAccess implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(SendDataToAccess.class);

    private final RestService restService;

    public SendDataToAccess(final RestService restService) {
        this.restService = restService;
    }

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        LOG.info("->Send data to E-Access.");
        RequestDto request = new RequestDto();
        request.setData(new String("hello from bpe"));
        ResponseDto response = restService.postData(TargetUrl.ACCESS, request);
        LOG.info((String)response.getData());
    }
}
