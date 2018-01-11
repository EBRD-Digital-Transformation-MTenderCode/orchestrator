package com.procurement.orchestrator.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class RequestNoticeDto {

    @JsonProperty(value = "cpid")
    @NonNull
    private String cpId;

    @JsonProperty(value = "ocid")
    @NonNull
    private String ocId;

    @JsonProperty(value = "stage")
    @NonNull
    private String stage;

    @JsonProperty(value = "tag")
    @NonNull
    private List<String> tag;

    @JsonProperty(value = "initiationType")
    @NonNull
    private String initiationType;

    @JsonProperty(value = "language")
    @NonNull
    private String language;

    @JsonProperty(value = "jsonData")
    @NonNull
    private JsonNode jsonData;

}
