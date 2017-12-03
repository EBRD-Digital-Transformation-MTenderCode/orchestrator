package com.procurement.orchestrator.rest;

import com.procurement.orchestrator.domain.dto.RequestDto;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

@Component
@RibbonClient(name = "access")
public class RestsClientImpl implements RestsClient {

    @LoadBalanced
    private final RestTemplate restTemplate;

    public RestsClientImpl(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ResponseDto postData(final RequestDto requestDto) {
        ResponseEntity<ResponseDto> result = restTemplate.postForEntity("/saveEIN", requestDto, ResponseDto.class);
        ResponseDto response = result.getBody();
        return response;
    }
}
