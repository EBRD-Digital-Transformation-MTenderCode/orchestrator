package com.procurement.orchestrator.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Params {
    @JsonProperty(value = "txId")
    private final String txId;
    @JsonProperty(value = "ocid")
    private final String ocid;
    @JsonProperty(value = "token")
    private String token;
    @JsonProperty(value = "processType")
    private final String processType;
    @JsonProperty(value = "owner")
    private final String owner;
    @JsonProperty(value = "country")
    private final String country;
    @JsonProperty(value = "pmd")
    private final String pmd;
}
