package com.procurement.orchestrator.domain.dto.cancellation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AmendmentDto {

    @JsonProperty(value = "description")
    private String description;

    @NotNull
    @JsonProperty(value = "rationale")
    private String rationale;

}
