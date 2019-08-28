package com.procurement.orchestrator.exception;

import lombok.Getter;

@Getter
public class MDMException extends RuntimeException {
    private final String responseBody;

    public MDMException(final String responseBody, Exception exception) {
        super(exception);
        this.responseBody = responseBody;
    }
}
