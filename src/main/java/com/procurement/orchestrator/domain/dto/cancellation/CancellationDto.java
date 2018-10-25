package com.procurement.orchestrator.domain.dto.cancellation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CancellationDto {

    @NotNull
    @NotEmpty
    @Valid
    @JsonProperty(value = "amendments")
    private Set<AmendmentDto> amendments;
}
