package com.procurement.orchestrator.domain.dto.auction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ElectronicAuctionProgress {

    @JsonProperty(value = "id")
    private String id;

    @JsonProperty(value = "period")
    private AuctionPeriod period;

    @JsonProperty(value = "breakdown")
    private Set<AuctionBreakdown> breakdown;

    public ElectronicAuctionProgress(@JsonProperty("id") final String id,
                                     @JsonProperty("period") final AuctionPeriod period,
                                     @JsonProperty("breakdown") final Set<AuctionBreakdown> breakdown) {
        this.id = id;
        this.period = period;
        this.breakdown = breakdown;
    }
}
