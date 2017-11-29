package com.procurement.orchestrator.exception;

import lombok.Getter;

@Getter
public class OperationException extends RuntimeException {

    private final String message;

    public OperationException(final String message) {
        this.message = message;
    }
}
