package com.procurement.orchestrator.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class EntityAccess {

    @JsonProperty(value = "entityType")
    private String entityType;
    @JsonProperty("entityId")
    private String entityId;
    @JsonProperty("entityToken")
    private String entityToken;

    @JsonCreator
    public EntityAccess(@JsonProperty("entityType") final String entityType,
                        @JsonProperty("entityId") final String entityId,
                        @JsonProperty("entityToken") final String entityToken) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.entityToken = entityToken;
    }
}
