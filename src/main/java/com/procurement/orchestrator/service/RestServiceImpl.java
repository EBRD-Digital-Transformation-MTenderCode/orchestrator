package com.procurement.orchestrator.service;

import com.procurement.orchestrator.model.dto.RequestDto;
import com.procurement.orchestrator.model.dto.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RestServiceImpl implements RestService {

    private final RestTemplate restTemplate;

    public RestServiceImpl(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ResponseDto sendData(RequestDto requestDto) {
        ResponseEntity<ResponseDto> response = restTemplate.postForEntity("url", requestDto, ResponseDto.class);
        return response.getBody();
    }
}
