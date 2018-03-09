package com.procurement.orchestrator.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class ResponseDto<T> {

    @JsonProperty(value = "success")
    private Boolean success;

    @JsonProperty(value = "details")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ResponseDetailsDto> details;

    @JsonProperty(value = "data")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public ResponseDto(@JsonProperty("success") final Boolean success,
                       @JsonProperty("details") final List<ResponseDetailsDto> details,
                       @JsonProperty("data") final T data) {
        this.success = success;
        this.details = details;
        this.data = data;
    }
}
