package com.procurement.orchestrator.delegate;

import com.procurement.orchestrator.domain.dto.RequestDto;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import com.procurement.orchestrator.rest.AccessRestClient;
import java.util.HashMap;
import java.util.Map;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class SendDataToAccess implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(SendDataToAccess.class);

    private final AccessRestClient accessRestClient;

    public SendDataToAccess(final AccessRestClient accessRestClient) {
        this.accessRestClient = accessRestClient;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info("->Send data to E-Access.");
        RequestDto request = new RequestDto();
        Map<String, String> data = new HashMap<>();
        data.put("message", "hello from bpe");
        request.setData(data);
        try {
            ResponseEntity<ResponseDto> responseEntity = accessRestClient.postData(request);
            ResponseDto response = responseEntity.getBody();
            LOG.info("->Get response: " + response.getData().toString());
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }
}
