package com.procurement.orchestrator.infrastructure.client

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.infrastructure.model.Version

class Command<P>(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: CommandId,
    @field:JsonProperty("version") @param:JsonProperty("version") val version: Version,
    @field:JsonProperty("action") @param:JsonProperty("action") val action: String,
    @field:JsonProperty("params") @param:JsonProperty("params") val params: P
)
