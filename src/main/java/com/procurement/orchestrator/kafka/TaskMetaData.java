package com.procurement.orchestrator.kafka;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TaskMetaData {

    @JsonProperty("processType")
    private String processType;

    @JsonProperty("txId")
    private String txId;

    @JsonCreator
    public TaskMetaData(@JsonProperty("processType") final String processType,
                        @JsonProperty("txId") final String txId) {
        this.processType = processType;
        this.txId = txId;
    }
}


