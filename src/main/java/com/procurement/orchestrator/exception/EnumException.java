package com.procurement.orchestrator.exception;

import lombok.Getter;

@Getter
public class EnumException extends RuntimeException {

    private final String message;

    public EnumException(final String enumType, final String value, final String values) {
        this.message = ("Unknown value for enumType " + enumType + ": " + value + ", Allowed values are " + values);
    }
}

