package com.procurement.orchestrator.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class PlatformMessage {

    @JsonProperty(value = "initiator")
    private String initiator;

    @JsonProperty("X-OPERATION-ID")
    private String operationId;

    @JsonProperty("X-RESPONSE-ID")
    private String responseId;

    @JsonProperty("data")
    private PlatformMessageData data;

    @JsonProperty("errors")
    private List<PlatformError> errors;
}
