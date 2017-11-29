package com.procurement.orchestrator.service;

import com.procurement.orchestrator.model.dto.RequestDto;
import com.procurement.orchestrator.model.dto.ResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface RestService {
    ResponseDto sendData(RequestDto requestDto);
}
