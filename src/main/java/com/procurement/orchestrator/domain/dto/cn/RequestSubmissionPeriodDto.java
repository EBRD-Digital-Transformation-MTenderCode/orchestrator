package com.procurement.orchestrator.domain.dto.cn;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({
        "ocid",
        "country",
        "procurementMethodDetails",
        "tenderPeriod"
})
public class RequestSubmissionPeriodDto {

    @JsonProperty("ocid")
    private String ocId;

    @JsonProperty("country")
    private String country;

    @JsonProperty("procurementMethodDetails")
    private String procurementMethodDetails;

    @JsonProperty("tenderPeriod")
    private TenderPeriodDto tenderPeriod;

    public RequestSubmissionPeriodDto(final String ocId,
                                      final String country,
                                      final String procurementMethodDetails,
                                      final TenderPeriodDto tenderPeriod) {
        this.ocId = ocId;
        this.country = country;
        this.procurementMethodDetails = procurementMethodDetails;
        this.tenderPeriod = tenderPeriod;
    }


}