package com.procurement.orchestrator.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class PlatformMessageData {

    @JsonProperty(value = "ocid")
    private String ocid;

    @JsonProperty("url")
    private String url;

    @JsonProperty("operationDate")
    private String operationDate;

    @JsonProperty("outcomes")
    private JsonNode outcomes;
}