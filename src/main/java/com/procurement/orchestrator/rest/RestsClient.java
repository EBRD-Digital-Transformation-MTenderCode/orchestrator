package com.procurement.orchestrator.rest;

import com.procurement.orchestrator.domain.dto.RequestDto;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public interface RestsClient {
    ResponseDto postData(RequestDto requestDto);
}
