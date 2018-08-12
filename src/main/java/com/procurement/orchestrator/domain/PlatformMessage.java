package com.procurement.orchestrator.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class PlatformMessage {

    @JsonProperty(value = "success")
    private Boolean success;
    @JsonProperty("X-OPERATION-ID")
    private String operationId;
    @JsonProperty("operationType")
    private String operationType;
    @JsonProperty("access")
    private List<EntityAccess> access;
    @JsonProperty("cpid")
    private String cpid;
    @JsonProperty("stage")
    private String stage;
    @JsonProperty("message")
    private String message;

    @JsonCreator
    public PlatformMessage(@JsonProperty("success") final Boolean success,
                           @JsonProperty("X-OPERATION-ID") final String operationId,
                           @JsonProperty("operationType") final String operationType,
                           @JsonProperty("access") final List<EntityAccess> access,
                           @JsonProperty("cpid") final String cpid,
                           @JsonProperty("stage") final String stage,
                           @JsonProperty("message") final String message) {
        this.success = success;
        this.operationId = operationId;
        this.operationType = operationType;
        this.access = access;
        this.cpid = cpid;
        this.stage = stage;
        this.message = message;
    }

    @JsonCreator
    public PlatformMessage(@JsonProperty("success") final Boolean success,
                           @JsonProperty("X-OPERATION-ID") final String operationId,
                           @JsonProperty("operationType") final String operationType,
                           @JsonProperty("cpid") final String cpid,
                           @JsonProperty("stage") final String stage,
                           @JsonProperty("message") final String message) {
        this.success = success;
        this.operationId = operationId;
        this.operationType = operationType;
        this.cpid = cpid;
        this.stage = stage;
        this.message = message;
    }
}
