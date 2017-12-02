package com.procurement.orchestrator.service;

import com.procurement.orchestrator.domain.dto.RequestDto;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestServiceImpl implements RestService {

    private final RestTemplate restTemplate;

    public RestServiceImpl(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ResponseDto postData(final RequestDto requestDto) {
        ResponseEntity<ResponseDto> result = restTemplate.postForEntity("", requestDto, ResponseDto.class);
        ResponseDto response = result.getBody();
        return response;
    }
}
