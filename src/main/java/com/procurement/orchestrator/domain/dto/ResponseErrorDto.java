package com.procurement.orchestrator.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ResponseErrorDto {

    @JsonProperty("code")
    private String code;

    @JsonProperty("description")
    private String description;

    public ResponseErrorDto(@JsonProperty("code") final String code, @JsonProperty("description") final String description) {
        this.code = code;
        this.description = description;
    }
}
