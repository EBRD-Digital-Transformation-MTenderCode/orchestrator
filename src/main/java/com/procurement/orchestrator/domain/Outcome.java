package com.procurement.orchestrator.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class Outcome {

    @JsonProperty("id")
    private String id;

    @JsonProperty("token")
    private String token;

    @JsonProperty("type")
    private String type;

    @JsonCreator
    public Outcome(@JsonProperty("id") final String id,
                   @JsonProperty("token") final String token,
                   @JsonProperty("type") final String type
    ) {
        this.id = id;
        this.token = token;
        this.type = type;
    }
}
