package com.procurement.orchestrator.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import lombok.Data;

@Data
public class CommandMessage {

    @JsonProperty(value = "id")
    private String id;

    @JsonProperty(value = "command")
    private Enum command;

    @JsonProperty(value = "context")
    private Context context;

    @JsonProperty(value = "data")
    private JsonNode data;

    @JsonProperty(value = "version")
    private ApiVersion version;

    public CommandMessage(@JsonProperty("id") final String id,
                          @JsonProperty("command") final Enum command,
                          @JsonProperty("context") final Context context,
                          @JsonProperty("data") final JsonNode data,
                          @JsonProperty("version") final ApiVersion version) {
        this.id = id;
        this.command = command;
        this.context = context;
        this.data = data;
        this.version = version;
    }
}
