package com.procurement.orchestrator.application.service

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.infrastructure.client.Command
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.util.*

interface ProceduralAction<P> {
    val version: Version
    val name: String

    fun build(id: CommandId = generateId(), params: P): Command<P> =
        Command(id = id, version = version, action = name, params = params)

    companion object {
        fun generateId(): CommandId = UUID.randomUUID().toString()
    }
}

interface FunctionalAction<P, R> : ProceduralAction<P> {
    val target: Target<R>
}
