package com.procurement.orchestrator.domain.model.submission

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

class Submissions(
    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("details") @param:JsonProperty("details") val details: Details = Details()
)
