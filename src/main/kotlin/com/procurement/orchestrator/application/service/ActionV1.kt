package com.procurement.orchestrator.application.service

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.infrastructure.client.CommandV1
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version

interface ProceduralActionV1<C, D> {
    val version: Version
    val name: String

    fun build(id: CommandId, context: C, data: D): CommandV1<C, D> =
      CommandV1(id = id, version = version, command = name, context = context, data = data)
}

interface FunctionalActionV1<C, D, R> : ProceduralActionV1<C, D> {
    val target: Target<R>
}
