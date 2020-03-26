package com.procurement.orchestrator.application.service

interface Logger {

    val isDebugEnabled: Boolean

    fun error(message: String, mdc: Map<String, String> = emptyMap(), exception: Exception? = null)

    fun warn(message: String, mdc: Map<String, String> = emptyMap(), exception: Exception? = null)

    fun info(message: String, mdc: Map<String, String> = emptyMap(), exception: Exception? = null)

    fun debug(message: String, mdc: Map<String, String> = emptyMap())
}
