package com.procurement.orchestrator.domain.dto.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.exception.EnumException;
import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    public enum ApiVersion {
        V_0_0_1("0.0.1");

        private static final Map<String, ApiVersion> CONSTANTS = new HashMap<>();
        private final String value;

        static {
            for (final ApiVersion c : values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        ApiVersion(final String value) {
            this.value = value;
        }

        @JsonCreator
        public static ApiVersion fromValue(final String value) {
            final ApiVersion constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new EnumException(ApiVersion.class.getName(), value, Arrays.toString(values()));
            }
            return constant;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }
    }

}
