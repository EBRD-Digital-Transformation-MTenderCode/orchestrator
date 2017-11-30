package com.procurement.orchestrator.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ResponseDetailsDto {

    @JsonProperty("code")
    private int code;

    @JsonProperty("message")
    private String message;

    public ResponseDetailsDto(@JsonProperty("code") final int code, @JsonProperty("message") final String message) {
        this.code = code;
        this.message = message;
    }
}
