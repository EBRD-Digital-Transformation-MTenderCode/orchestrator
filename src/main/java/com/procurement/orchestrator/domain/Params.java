package com.procurement.orchestrator.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Params {
    @JsonProperty(value = "operationId")
    private String operationId;
    @JsonProperty(value = "cpid")
    private String cpid;
    @JsonProperty(value = "ocid")
    private String ocid;
    @JsonProperty(value = "processType")
    private String processType;
    @JsonProperty(value = "owner")
    private String owner;
    @JsonProperty(value = "country")
    private String country;
    @JsonProperty(value = "pmd")
    private String pmd;
    @JsonProperty(value = "token")
    private String token;
}
