package com.procurement.orchestrator.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Notification {

    @JsonProperty(value = "platformId")
    private UUID platformId;
    @JsonProperty("operationId")
    private UUID operationId;
    @JsonProperty("message")
    private String message;

    @JsonCreator
    public Notification(@JsonProperty("platformId") final UUID platformId,
                        @JsonProperty("operationId") final UUID operationId,
                        @JsonProperty("message") final String message) {
        this.platformId = platformId;
        this.operationId = operationId;
        this.message = message;
    }
}

