package com.procurement.orchestrator.service;

import com.procurement.orchestrator.domain.dto.RequestDto;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface RestService {
    ResponseDto postData(RequestDto requestDto);
}
