package com.procurement.orchestrator.service;

import com.procurement.orchestrator.domain.constant.TargetUrl;
import com.procurement.orchestrator.domain.dto.RequestDto;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import org.springframework.web.client.RestTemplate;

public class RestServiceImpl implements RestService {

    private final RestTemplate restTemplate;

    public RestServiceImpl(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ResponseDto postData(final TargetUrl url, final RequestDto requestDto) {
        return restTemplate.postForEntity(
            url.value(),
            requestDto,
            ResponseDto.class)
                           .getBody();
    }
}
