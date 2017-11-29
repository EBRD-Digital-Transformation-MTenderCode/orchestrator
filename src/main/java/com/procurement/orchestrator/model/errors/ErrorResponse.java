package com.procurement.orchestrator.model.errors;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ErrorResponse {

    @JsonProperty("code")
    private int code;

    @JsonProperty("message")
    private String message;

    public ErrorResponse(@JsonProperty("code") final int code, @JsonProperty("message") final String message) {
        this.code = code;
        this.message = message;
    }
}
