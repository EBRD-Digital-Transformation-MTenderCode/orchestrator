package com.procurement.orchestrator.kafka;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TaskMetaData {

    @JsonProperty("processType")
    private String processType;

    @JsonProperty("operationId")
    private String operationId;

    @JsonCreator
    public TaskMetaData(@JsonProperty("processType") final String processType,
                        @JsonProperty("operationId") final String operationId) {
        this.processType = processType;
        this.operationId = operationId;
    }
}


