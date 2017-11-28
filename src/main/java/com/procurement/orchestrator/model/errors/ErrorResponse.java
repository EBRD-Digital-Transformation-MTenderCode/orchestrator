package com.procurement.orchestrator.model.errors;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ErrorResponse {

    @JsonProperty("message")
    private String message;

    public ErrorResponse(@JsonProperty("message") final String message) {
        this.message = message;
    }
}
