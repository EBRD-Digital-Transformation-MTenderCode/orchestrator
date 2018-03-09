package com.procurement.orchestrator.domain.chronograph;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChronographResponse {

    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private ChronographResponseData data;

    @Data
    public class ChronographResponseData {

        @JsonProperty("ocid")
        private String ocid;

        @JsonProperty("phase")
        private String phase;

        @JsonProperty("launchTime")
        private String launchTime;

        @JsonProperty("metaData")
        private String metaData;
    }
}


