package com.procurement.orchestrator.cassandra.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Params {
    @JsonProperty(value = "operationId")
    private String operationId;
    @JsonProperty(value = "requestId")
    private String requestId;
    @JsonProperty(value = "cpid")
    private String cpid;
    @JsonProperty(value = "ocid")
    private String ocid;
    @JsonProperty(value = "stage")
    private String stage;
    @JsonProperty(value = "processType")
    private String processType;
    @JsonProperty(value = "operationType")
    private String operationType;
    @JsonProperty(value = "owner")
    private String owner;
    @JsonProperty(value = "country")
    private String country;
    @JsonProperty(value = "pmd")
    private String pmd;
    @JsonProperty(value = "token")
    private String token;
    @JsonProperty(value = "startDate")
    private String startDate;
    @JsonProperty(value = "endDate")
    private String endDate;
}
