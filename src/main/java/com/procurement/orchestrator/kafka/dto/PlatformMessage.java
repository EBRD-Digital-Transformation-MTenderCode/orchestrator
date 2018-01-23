package com.procurement.orchestrator.kafka.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PlatformMessage {

    @JsonProperty("operationId")
    private String operationId;

    @JsonProperty("data")
    private String data;

    @JsonCreator
    public PlatformMessage(@JsonProperty("operationId") final String operationId,
                           @JsonProperty("data") final String data) {
        this.operationId = operationId;
        this.data = data;
    }

}
