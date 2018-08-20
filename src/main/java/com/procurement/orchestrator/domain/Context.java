package com.procurement.orchestrator.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Context {
    @JsonProperty(value = "operationId")
    private String operationId;

    @JsonProperty(value = "requestId")
    private String requestId;

    @JsonProperty(value = "responseId")
    private String responseId;

    @JsonProperty(value = "cpid")
    private String cpid;

    @JsonProperty(value = "ocid")
    private String ocid;

    @JsonProperty(value = "stage")
    private String stage;

    @JsonProperty(value = "prevStage")
    private String prevStage;

    @JsonProperty(value = "processType")
    private String processType;

    @JsonProperty(value = "operationType")
    private String operationType;

    @JsonProperty(value = "phase")
    private String phase;

    @JsonProperty(value = "owner")
    private String owner;

    @JsonProperty(value = "country")
    private String country;

    @JsonProperty(value = "language")
    private String language;

    @JsonProperty(value = "pmd")
    private String pmd;

    @JsonProperty(value = "token")
    private String token;

    @JsonProperty("initiator")
    private String initiator;

    @JsonProperty("data")
    private PlatformMessageData data;

    @JsonProperty("errors")
    private List<PlatformError> errors;

    @JsonProperty(value = "startDate")
    private String startDate;

    @JsonProperty(value = "endDate")
    private String endDate;

    @JsonProperty(value = "id")
    private String id;

    @JsonProperty(value = "setExtendedPeriod")
    private Boolean setExtendedPeriod = false;

    public void setOperationId(final String operationId) {
        this.operationId = UUID.fromString(operationId).toString();
    }

    public void setOwner(final String owner) {
        this.owner = UUID.fromString(owner).toString();
    }
}
