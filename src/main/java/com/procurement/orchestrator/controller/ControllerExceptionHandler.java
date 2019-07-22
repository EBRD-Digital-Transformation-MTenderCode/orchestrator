package com.procurement.orchestrator.controller;

import com.procurement.orchestrator.domain.ExternalException;
import com.procurement.orchestrator.domain.PlatformError;
import com.procurement.orchestrator.exception.EnumException;
import com.procurement.orchestrator.exception.OperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashSet;
import java.util.Set;

@ControllerAdvice
public class ControllerExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(OperationException.class)
    public ResponseEntity<ExternalException> handleOperationException(final OperationException e) {
        final String errorMessage = e.getMessage();
        if(LOG.isWarnEnabled())
            LOG.warn(errorMessage, e);
        return new ResponseEntity<>(getError(errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler(EnumException.class)
    public ResponseEntity<ExternalException> handleEnumException(final EnumException e) {
        final String errorMessage = e.getMessage();
        if(LOG.isWarnEnabled())
            LOG.warn(errorMessage, e);
        return new ResponseEntity<>(getError(errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExternalException> handleException(final Exception e) {
        final String errorMessage = e.getMessage();
        if(LOG.isWarnEnabled())
            LOG.warn(errorMessage, e);
        return new ResponseEntity<>(getError(errorMessage), HttpStatus.BAD_REQUEST);
    }

    private ExternalException getError(final String msg) {
        final Set<PlatformError> errors = new HashSet<>();
        errors.add(new PlatformError("400.00.00.00", msg));
        return new ExternalException(errors);
    }

}
