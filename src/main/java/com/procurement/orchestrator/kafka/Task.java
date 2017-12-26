package com.procurement.orchestrator.kafka;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Task {

    private String requestId;
    private Key key;
    private LocalDateTime launchTime;
    private String metaData;

    @JsonProperty("id")
    private String id;

    @JsonProperty("phase")
    private String phase;


    @JsonCreator
    public Task(@JsonProperty("id") final String id,
                @JsonProperty("phase") final String phase) {
        this.id = id;
        this.phase = phase;
    }

   public class Key {
       private String ocid;
       private String phase;
   }
}


