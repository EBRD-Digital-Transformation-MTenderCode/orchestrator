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
public class AuctionTender {

    @JsonProperty(value = "id")
    private String id;

    @JsonProperty(value = "electronicAuctions")
    private ElectronicAuctions electronicAuctions;

    @JsonProperty(value = "auctionPeriod")
    private AuctionPeriod auctionPeriod;

    public AuctionTender(@JsonProperty("id") final String id,
                         @JsonProperty("electronicAuctions") final ElectronicAuctions electronicAuctions,
                         @JsonProperty("auctionPeriod") final AuctionPeriod auctionPeriod) {
        this.id = id;
        this.electronicAuctions = electronicAuctions;
        this.auctionPeriod = auctionPeriod;
    }
}
