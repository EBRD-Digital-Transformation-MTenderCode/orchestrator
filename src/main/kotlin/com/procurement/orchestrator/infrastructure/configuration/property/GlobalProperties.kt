package com.procurement.orchestrator.infrastructure.configuration.property

import com.procurement.orchestrator.infrastructure.extension.io.orThrow
import java.util.*

object GlobalProperties {
    val service = Service()

    class Service {
        val id: String = "1"
        val name: String = "bpe"
        val version: String = loadVersion()

        private fun loadVersion(): String {
            val gitProps: Properties = try {
                GlobalProperties.javaClass.getResourceAsStream("/git.properties")
                    .use { stream ->
                        Properties().apply { load(stream) }
                    }
            } catch (expected: Exception) {
                throw IllegalStateException("File 'git.properties' cannot be loaded.", expected)
            }
            return gitProps.orThrow("git.commit.id.abbrev")
        }
    }
}
