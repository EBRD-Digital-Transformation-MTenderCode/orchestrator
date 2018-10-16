package com.procurement.orchestrator.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ExternalError {

    @JsonProperty("code")
    private String code;

    @JsonProperty("description")
    private String description;

    @JsonCreator
    public ExternalError(@JsonProperty("code") final String code,
                         @JsonProperty("description") final String description) {
        this.code = code;
        this.description = description;
    }
}
