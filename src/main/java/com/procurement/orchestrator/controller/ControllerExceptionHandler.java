package com.procurement.orchestrator.controller;

import com.procurement.orchestrator.domain.dto.ResponseDetailsDto;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import com.procurement.orchestrator.exception.OperationException;
import java.util.Collections;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(OperationException.class)
    public ResponseDto handleValidationContractProcessPeriod(final OperationException e) {
        return new ResponseDto(false,
                               Collections.singletonList(new ResponseDetailsDto(
                                   HttpStatus.BAD_REQUEST.value(),
                                   e.getMessage())), null);
    }
}
