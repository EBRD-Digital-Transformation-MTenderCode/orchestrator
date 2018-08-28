package com.procurement.orchestrator.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "X-OPERATION-ID",
        "X-RESPONSE-ID",
        "initiator",
        "data",
        "errors"
})
public class PlatformMessage {

    @JsonProperty("X-OPERATION-ID")
    private String operationId;

    @JsonProperty("X-RESPONSE-ID")
    private String responseId;

    @JsonProperty(value = "initiator")
    private String initiator;

    @JsonProperty("data")
    private PlatformMessageData data;

    @JsonProperty("errors")
    private Set<PlatformError> errors;
}
