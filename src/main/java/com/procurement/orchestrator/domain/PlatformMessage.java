package com.procurement.orchestrator.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class PlatformMessage {

    @JsonProperty(value = "success")
    private Boolean success;
    @JsonProperty("operationId")
    private String operationId;
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
                           @JsonProperty("operationId") final String operationId,
                           @JsonProperty("access") final List<EntityAccess> access,
                           @JsonProperty("cpid") final String cpid,
                           @JsonProperty("stage") final String stage,
                           @JsonProperty("message") final String message) {
        this.success = success;
        this.operationId = operationId;
        this.access = access;
        this.cpid = cpid;
        this.stage = stage;
        this.message = message;
    }

    @JsonCreator
    public PlatformMessage(@JsonProperty("success") final Boolean success,
                           @JsonProperty("operationId") final String operationId,
                           @JsonProperty("message") final String message) {
        this.success = success;
        this.operationId = operationId;
        this.message = message;
    }
}
