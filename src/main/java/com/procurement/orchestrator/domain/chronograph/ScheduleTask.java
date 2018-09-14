package com.procurement.orchestrator.domain.chronograph;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.orchestrator.databinding.LocalDateTimeDeserializer;
import com.procurement.orchestrator.databinding.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ScheduleTask {

    @JsonProperty("action")
    private ActionType action;

    @JsonProperty("ocid")
    private String ocid;

    @JsonProperty("phase")
    private String phase;

    @JsonProperty("launchTime")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime launchTime;

    @JsonProperty("newLaunchTime")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime newLaunchTime;

    @JsonProperty("metaData")
    private String metaData;

    @JsonCreator
    public ScheduleTask(@JsonProperty("action") final ActionType action,
                        @JsonProperty("ocid") final String ocid,
                        @JsonProperty("phase") final String phase,
                        @JsonProperty("launchTime") final LocalDateTime launchTime,
                        @JsonProperty("newLaunchTime") final LocalDateTime newLaunchTime,
                        @JsonProperty("metaData") final String metaData) {
        this.action = action;
        this.ocid = ocid;
        this.phase = phase;
        this.launchTime = launchTime;
        this.newLaunchTime = newLaunchTime;
        this.metaData = metaData;
    }
}


