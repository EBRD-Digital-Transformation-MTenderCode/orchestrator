package com.procurement.orchestrator.delegate;

import com.procurement.orchestrator.domain.dto.RequestDto;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import com.procurement.orchestrator.rest.NoticeRestClient;
import java.util.HashMap;
import java.util.Map;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class SendDataToNotice implements JavaDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(SendDataToNotice.class);

    private final NoticeRestClient noticeRestClient;

    public SendDataToNotice(final NoticeRestClient noticeRestClient) {
        this.noticeRestClient = noticeRestClient;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info("->Send data to E-Notice.");
        RequestDto request = new RequestDto();
        Map<String, String> data = new HashMap<>();
        data.put("message", "hello from bpe");
        request.setData(data);
        try {
            ResponseEntity<ResponseDto> responseEntity = noticeRestClient.postData("23232", request);
            ResponseDto response = responseEntity.getBody();
            LOG.info("->Get response: " + response.getData().toString());
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }
}
