package com.procurement.orchestrator.domain.dto.auction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ElectronicAuctionResult {

    @JsonProperty(value = "relatedBid")
    private String relatedBid;

    @JsonProperty(value = "value")
    private AuctionValue value;


    public ElectronicAuctionResult(@JsonProperty("relatedBid") final String relatedBid,
                                   @JsonProperty("value") final AuctionValue value) {
        this.relatedBid = relatedBid;
        this.value = value;
    }
}
