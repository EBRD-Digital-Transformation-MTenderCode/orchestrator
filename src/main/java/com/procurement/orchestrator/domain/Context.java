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

    public static class Builder {
        private String operationId;
        private String requestId;
        private String cpid;
        private String ocid;
        private String ocidCn;
        private String stage;
        private String prevStage;
        private String processType;
        private String operationType;
        private String phase;
        private String owner;
        private String ownerCA;
        private String country;
        private String language;
        private String pmd;
        private String token;
        private Set<Outcome> outcomes;
        private Set<PlatformError> errors;
        private String startDate;
        private String endDate;
        private String id;
        private Long timeStamp;
        private Boolean isAuction;
        private Set<AuctionLinks> auctionLinks;
        private String mainProcurementCategory;
        private String awardCriteria;
        private Boolean testMode;

        public Builder setOperationId(String operationId) {
            this.operationId = operationId;
            return this;
        }

        public Builder setRequestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder setCpid(String cpid) {
            this.cpid = cpid;
            return this;
        }

        public Builder setOcid(String ocid) {
            this.ocid = ocid;
            return this;
        }

        public Builder setOcidCn(String ocidCn) {
            this.ocidCn = ocidCn;
            return this;
        }

        public Builder setStage(String stage) {
            this.stage = stage;
            return this;
        }

        public Builder setPrevStage(String prevStage) {
            this.prevStage = prevStage;
            return this;
        }

        public Builder setProcessType(String processType) {
            this.processType = processType;
            return this;
        }

        public Builder setOperationType(String operationType) {
            this.operationType = operationType;
            return this;
        }

        public Builder setPhase(String phase) {
            this.phase = phase;
            return this;
        }

        public Builder setOwner(String owner) {
            this.owner = owner;
            return this;
        }

        public Builder setOwnerCA(String ownerCA) {
            this.ownerCA = ownerCA;
            return this;
        }

        public Builder setCountry(String country) {
            this.country = country;
            return this;
        }

        public Builder setLanguage(String language) {
            this.language = language;
            return this;
        }

        public Builder setPmd(String pmd) {
            this.pmd = pmd;
            return this;
        }

        public Builder setToken(String token) {
            this.token = token;
            return this;
        }

        public Builder setOutcomes(Set<Outcome> outcomes) {
            this.outcomes = outcomes;
            return this;
        }

        public Builder setErrors(Set<PlatformError> errors) {
            this.errors = errors;
            return this;
        }

        public Builder setStartDate(String startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder setEndDate(String endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setTimeStamp(Long timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public Builder setIsAuction(Boolean isAuction) {
            this.isAuction = isAuction;
            return this;
        }

        public Builder setAuctionLinks(Set<AuctionLinks> auctionLinks) {
            this.auctionLinks = auctionLinks;
            return this;
        }

        public Builder setMainProcurementCategory(String mainProcurementCategory) {
            this.mainProcurementCategory = mainProcurementCategory;
            return this;
        }

        public Builder setAwardCriteria(String awardCriteria) {
            this.awardCriteria = awardCriteria;
            return this;
        }

        public Builder setTestMode(Boolean testMode) {
            this.testMode = testMode;
            return this;
        }

        public Context build() {
            return new Context(operationId, requestId, cpid, ocid, ocidCn, stage, prevStage, processType, operationType, phase, owner, ownerCA, country, language, pmd, token, outcomes, errors, startDate, endDate, id, timeStamp, isAuction, auctionLinks, mainProcurementCategory, awardCriteria, testMode);
        }
    }
}
