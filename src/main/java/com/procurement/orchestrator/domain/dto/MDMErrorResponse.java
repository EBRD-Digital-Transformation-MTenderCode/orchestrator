package com.procurement.orchestrator.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class MDMErrorResponse {
    private List<Error> errors;

    public MDMErrorResponse(@JsonProperty("errors") final List<Error> errors) {
        this.errors = errors;
    }

    @Getter
    public static class Error {
        private String code;
        private String description;

        public Error(@JsonProperty("code") final String code, @JsonProperty("description") final String description) {
            this.code = code;
            this.description = description;
        }
    }
}
