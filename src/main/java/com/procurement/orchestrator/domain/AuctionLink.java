package com.procurement.orchestrator.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuctionLink {

    @JsonProperty("relatedBid")
    private String relatedBid;

    @JsonProperty("url")
    private String url;

    @JsonCreator
    public AuctionLink(@JsonProperty("relatedBid") final String relatedBid,
                       @JsonProperty("url") final String url) {
        this.relatedBid = relatedBid;
        this.url = url;
    }
}

