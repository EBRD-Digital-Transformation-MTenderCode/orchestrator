package com.procurement.orchestrator.domain.dto.auction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.orchestrator.databinding.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuctionBreakdown {

    @JsonProperty(value = "relatedBid")
    private String relatedBid;

    @JsonProperty(value = "dateMet")
    @JsonDeserialize(using = AuctionJsonDateDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateMet;

    @JsonProperty(value = "value")
    private AuctionValue value;

    public AuctionBreakdown(@JsonProperty("relatedBid") final String relatedBid,
                            @JsonProperty("dateMet") final LocalDateTime dateMet,
                            @JsonProperty("value") final AuctionValue value) {
        this.relatedBid = relatedBid;
        this.dateMet = dateMet;
        this.value = value;
    }
}
