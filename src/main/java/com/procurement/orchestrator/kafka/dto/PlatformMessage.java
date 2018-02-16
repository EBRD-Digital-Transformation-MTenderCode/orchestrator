package com.procurement.orchestrator.kafka.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({
        "success",
        "operationId",
        "access",
        "ocid",
        "cpid"
})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class PlatformMessage {

    @JsonProperty(value = "success")
    private Boolean success;
    @JsonProperty("operationId")
    private String operationId;
    @JsonProperty("access")
    private String access;
    @JsonProperty("ocid")
    private String ocid;
    @JsonProperty("cpid")
    private String cpid;
    @JsonProperty("message")
    private String message;

    @JsonCreator
    public PlatformMessage(@JsonProperty("success") final Boolean success,
                           @JsonProperty("operationId") final String operationId,
                           @JsonProperty("access") final String access,
                           @JsonProperty("ocid") final String ocid,
                           @JsonProperty("cpid") final String cpid,
                           @JsonProperty("message") final String message) {
        this.success = success;
        this.operationId = operationId;
        this.access = access;
        this.ocid = ocid;
        this.cpid = cpid;
        this.message = message;
    }

    @JsonCreator
    public PlatformMessage(@JsonProperty("success") final Boolean success,
                           @JsonProperty("operationId") final String operationId,
                           @JsonProperty("cpid") final String message) {
        this.success = success;
        this.operationId = operationId;
        this.message = message;
    }

}
