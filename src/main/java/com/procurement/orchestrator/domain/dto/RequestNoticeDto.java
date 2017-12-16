package com.procurement.orchestrator.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.orchestrator.databinding.LocalDateTimeDeserializer;
import com.procurement.orchestrator.databinding.LocalDateTimeSerializer;
import java.time.LocalDateTime;
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

    @JsonProperty(value = "releaseId")
    @NonNull
    private String releaseId;

    @JsonProperty(value = "releaseDate")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime releaseDate;

    @JsonProperty(value = "jsonData")
    @NonNull
    private JsonNode jsonData;

}
