package com.procurement.orchestrator.controller;

import com.procurement.orchestrator.exception.OperationException;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ResponseBody
    @ExceptionHandler(OperationException.class)
    public ResponseEntity<String> handleOperationException(final OperationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(FeignException.class)
//    public void handleFeignException(final FeignException e) {
//        System.out.println(e.getMessage());
//    }
}
