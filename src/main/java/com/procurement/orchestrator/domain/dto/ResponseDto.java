package com.procurement.orchestrator.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto<T> {

    @JsonProperty(value = "errors")
    private List<ResponseErrorDto> errors;

    @JsonProperty(value = "data")
    private T data;

    public ResponseDto(
            @JsonProperty("errors") final List<ResponseErrorDto> errors,
            @JsonProperty("data") final T data) {
        this.errors = errors;
        this.data = data;
    }
}
