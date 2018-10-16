package com.procurement.orchestrator.controller;

import com.procurement.orchestrator.domain.ExternalException;
import com.procurement.orchestrator.domain.PlatformError;
import com.procurement.orchestrator.exception.EnumException;
import com.procurement.orchestrator.exception.OperationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashSet;
import java.util.Set;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ResponseBody
    @ExceptionHandler(OperationException.class)
    public ResponseEntity<ExternalException> handleOperationException(final OperationException e) {
        return new ResponseEntity<>(getError(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler(EnumException.class)
    public ResponseEntity<ExternalException> handleEnumException(final EnumException e) {
        return new ResponseEntity<>(getError(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExternalException> handleException(final Exception e) {
        return new ResponseEntity<>(getError(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    private ExternalException getError(final String msg) {
        final Set<PlatformError> errors = new HashSet<>();
        errors.add(new PlatformError("400.00.00.00", msg));
        final ExternalException externalException = new ExternalException(errors);
        return externalException;
    }

}
