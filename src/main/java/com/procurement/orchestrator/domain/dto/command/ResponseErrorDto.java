package com.procurement.orchestrator.domain.dto.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
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
