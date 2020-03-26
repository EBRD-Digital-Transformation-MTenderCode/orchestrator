package com.procurement.orchestrator.infrastructure.client

import com.fasterxml.jackson.annotation.JsonProperty

class Command<P>(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
    @field:JsonProperty("version") @param:JsonProperty("version") val version: String,
    @field:JsonProperty("action") @param:JsonProperty("action") val action: String,
    @field:JsonProperty("params") @param:JsonProperty("params") val params: P
)
