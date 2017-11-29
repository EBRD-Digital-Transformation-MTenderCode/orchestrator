package com.procurement.orchestrator.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.procurement.orchestrator.model.errors.ErrorResponse;
import java.util.List;
import lombok.Data;

@Data
public class ResponseDto<T> {

    @JsonProperty(value = "errors")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ErrorResponse> errors;

    @JsonProperty(value = "data")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
}
