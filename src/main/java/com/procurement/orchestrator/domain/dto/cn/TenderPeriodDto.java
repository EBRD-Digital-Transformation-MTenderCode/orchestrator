package com.procurement.orchestrator.domain.dto.cn;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.orchestrator.databinding.LocalDateTimeSerializer;

import java.time.LocalDateTime;

@JsonPropertyOrder({
        "startDate",
        "endDate"
})
public class TenderPeriodDto {

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("startDate")
    private LocalDateTime startDate;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("endDate")
    private LocalDateTime endDate;

    public TenderPeriodDto(final LocalDateTime startDate,
                           final LocalDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}