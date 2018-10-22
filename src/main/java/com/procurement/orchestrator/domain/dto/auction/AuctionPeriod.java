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
public class AuctionPeriod {

    @JsonProperty(value = "startDate")
    @JsonDeserialize(using = AuctionJsonDateDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startDate;

    @JsonProperty(value = "endDate")
    @JsonDeserialize(using = AuctionJsonDateDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime endDate;

    public AuctionPeriod(@JsonProperty("startDate") final LocalDateTime startDate,
                         @JsonProperty("endDate") final LocalDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
