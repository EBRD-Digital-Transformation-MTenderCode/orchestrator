package com.procurement.orchestrator.model.errors;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidationErrorResponse {
    private String message;
    private List<ErrorPoint> errors;

    public ValidationErrorResponse(final String message, final List<ErrorPoint> errors) {
        this.message = message;
        this.errors = errors;
    }

    @Getter
    public static class ErrorPoint {
        private String field;
        private String message;
        private String code;

        public ErrorPoint(final String field, final String message, final String code) {
            this.field = field;
            this.message = message;
            this.code = code;
        }
    }
}
