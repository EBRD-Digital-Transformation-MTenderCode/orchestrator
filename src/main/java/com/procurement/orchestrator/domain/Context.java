package com.procurement.orchestrator.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

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

    @JsonProperty(value = "cpid")
    private String cpid;

    @JsonProperty(value = "ocid")
    private String ocid;

    @JsonProperty(value = "ocidCn")
    private String ocidCn;

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

    @JsonProperty(value = "ownerCA")
    private String ownerCA;

    @JsonProperty(value = "country")
    private String country;

    @JsonProperty(value = "language")
    private String language;

    @JsonProperty(value = "pmd")
    private String pmd;

    @JsonProperty(value = "token")
    private String token;

    @JsonProperty("outcomes")
    private Set<Outcome> outcomes;

    @JsonProperty("errors")
    private Set<PlatformError> errors;

    @JsonProperty(value = "startDate")
    private String startDate;

    @JsonProperty(value = "endDate")
    private String endDate;

    @JsonProperty(value = "id")
    private String id;

    @JsonProperty(value = "timeStamp")
    private Long timeStamp;

    @JsonProperty(value = "isAuction")
    private Boolean isAuction = false;

    @JsonProperty(value = "auctionLinks")
    private Set<AuctionLinks> auctionLinks;

    @JsonProperty(value = "mainProcurementCategory")
    private String mainProcurementCategory;

    @JsonProperty(value = "awardCriteria")
    private String awardCriteria;

    @JsonProperty(value = "testMode")
    private Boolean testMode;

    public void setOperationId(final String operationId) {
        this.operationId = UUID.fromString(operationId).toString();
    }

    public void setOwner(final String owner) {
        this.owner = UUID.fromString(owner).toString();
    }

    public String getCpid() {
        return cpid;
    }

    public String getOperationType() {
        return operationType;
    }

    public String getProcessType() {
        return processType;
    }

    public String getRequestId() {
        return requestId;
    }
}
