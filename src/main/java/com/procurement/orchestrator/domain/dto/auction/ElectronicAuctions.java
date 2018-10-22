package com.procurement.orchestrator.domain.dto.auction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ElectronicAuctions {

    @JsonProperty(value = "details")
    private Set<ElectronicAuctionsDetails> details;

    public ElectronicAuctions(@JsonProperty("details") final Set<ElectronicAuctionsDetails> details) {
        this.details = details;
    }
}
