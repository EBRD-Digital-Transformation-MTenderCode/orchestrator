package com.procurement.orchestrator.kafka;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Message {

    @JsonProperty("id")
    private String id;

    @JsonProperty("phase")
    private String phase;


    @JsonCreator
    public Message(@JsonProperty("id") final String id,
                   @JsonProperty("phase") final String phase) {
        this.id = id;
        this.phase = phase;
    }
}
