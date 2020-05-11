package com.procurement.orchestrator.application.model.context

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.context.container.PropertyContainer
import com.procurement.orchestrator.application.model.context.property.propertyDelegate

class CamundaContext(propertyContainer: PropertyContainer) {

    var request: Request by propertyDelegate(propertyContainer)

    class Request(

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String? = null,

        @field:JsonProperty("payload") @param:JsonProperty("payload") val payload: String
    )
}
