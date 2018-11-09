package com.procurement.orchestrator.domain.dto.auction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuctionData {

    @JsonProperty(value = "tender")
    private AuctionTender tender;

    public AuctionData(@JsonProperty("tender") final AuctionTender tender) {
        this.tender = tender;
    }
}
