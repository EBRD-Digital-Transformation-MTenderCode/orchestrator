package com.procurement.orchestrator.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ResponseDto<T> {

    @JsonProperty(value = "success")
    private Boolean success;

    @JsonProperty(value = "details")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ResponseDetailsDto> details;

    @JsonProperty(value = "errors")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ResponseErrorDto> errors;

    @JsonProperty(value = "data")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public ResponseDto(@JsonProperty("success") final Boolean success,
                       @JsonProperty("details") final List<ResponseDetailsDto> details,
                       @JsonProperty("errors") final List<ResponseErrorDto> errors,
                       @JsonProperty("data") final T data) {
        this.success = success;
        this.details = details;
        this.errors = errors;
        this.data = data;
    }
}
