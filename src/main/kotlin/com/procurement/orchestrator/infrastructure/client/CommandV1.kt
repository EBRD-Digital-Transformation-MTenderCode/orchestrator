package com.procurement.orchestrator.infrastructure.client

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.infrastructure.model.Version

class CommandV1<C, D>(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: CommandId,
    @field:JsonProperty("version") @param:JsonProperty("version") val version: Version,
    @field:JsonProperty("command") @param:JsonProperty("command") val command: String,
    @field:JsonProperty("context") @param:JsonProperty("context") val context: C,
    @field:JsonProperty("data") @param:JsonProperty("data") val data: D
)
