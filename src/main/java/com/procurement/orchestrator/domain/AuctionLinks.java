package com.procurement.orchestrator.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class AuctionLinks {

    @JsonProperty("owner")
    private String owner;

    @JsonProperty("links")
    private Set<AuctionLink> links;

    @JsonCreator
    public AuctionLinks(@JsonProperty("owner") final String owner,
                        @JsonProperty("links") final Set<AuctionLink> links) {
        this.owner = owner;
        this.links = links;
    }
}

