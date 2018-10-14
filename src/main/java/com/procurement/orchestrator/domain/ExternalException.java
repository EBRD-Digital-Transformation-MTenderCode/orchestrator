package com.procurement.orchestrator.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ExternalException {

    @JsonProperty("errors")
    private Set<PlatformError> errors;

    @JsonCreator
    public ExternalException(@JsonProperty("errors") final Set<PlatformError> errors) {
        this.errors = errors;
    }
}
