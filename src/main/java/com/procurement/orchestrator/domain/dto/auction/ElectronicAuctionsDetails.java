package com.procurement.orchestrator.domain.dto.auction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.ApiVersion;
import lombok.Data;

import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ElectronicAuctionsDetails {

    @JsonProperty(value = "id")
    private String id;

    @JsonProperty(value = "relatedLot")
    private String relatedLot;

    @JsonProperty(value = "auctionPeriod")
    private AuctionPeriod auctionPeriod;

    @JsonProperty(value = "electronicAuctionProgress")
    private Set<ElectronicAuctionProgress> electronicAuctionProgress;

    @JsonProperty(value = "electronicAuctionResult")
    private Set<ElectronicAuctionResult> electronicAuctionResult;

    public ElectronicAuctionsDetails(@JsonProperty("id") final String id,
                                     @JsonProperty("relatedLot") final String relatedLot,
                                     @JsonProperty("auctionPeriod") final AuctionPeriod auctionPeriod,
                                     @JsonProperty("electronicAuctionProgress") final Set<ElectronicAuctionProgress> electronicAuctionProgress,
                                     @JsonProperty("electronicAuctionResult") final Set<ElectronicAuctionResult> electronicAuctionResult) {
        this.id = id;
        this.relatedLot = relatedLot;
        this.auctionPeriod = auctionPeriod;
        this.electronicAuctionProgress = electronicAuctionProgress;
        this.electronicAuctionResult = electronicAuctionResult;
    }
}
