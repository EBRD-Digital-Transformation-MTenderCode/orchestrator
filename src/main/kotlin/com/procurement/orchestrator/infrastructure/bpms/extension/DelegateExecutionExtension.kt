package com.procurement.orchestrator.infrastructure.bpms.extension

import com.procurement.orchestrator.application.model.context.container.PropertyContainer
import com.procurement.orchestrator.application.model.context.members.Errors
import com.procurement.orchestrator.application.model.context.members.Incident
import com.procurement.orchestrator.domain.functional.None
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Some
import org.camunda.bpm.engine.delegate.DelegateExecution

private const val UPDATE_GLOBAL_CONTEXT = "UGC"
private const val RESULT_VARIABLE_NAME = "result"
private const val ERRORS_VARIABLE_NAME = "errors"
private const val INCIDENT_VARIABLE_NAME = "incident"

val DelegateExecution.isUpdateGlobalContext: Boolean
    get() = getVariable(UPDATE_GLOBAL_CONTEXT)?.toString()?.toBoolean() ?: true

fun DelegateExecution.asPropertyContainer() = object : PropertyContainer {

    override fun get(name: String): Any? = this@asPropertyContainer.getVariable(name)

    override fun set(name: String, value: Any) {
        this@asPropertyContainer.setVariable(name, value)
    }
}

fun <T> DelegateExecution.setResult(value: Option<T>) {
    when (value) {
        is None -> this.setVariable(RESULT_VARIABLE_NAME, null)
        is Some -> when (value.get) {
            is Unit -> this.setVariable(RESULT_VARIABLE_NAME, null)
            else -> this.setVariable(RESULT_VARIABLE_NAME, value.get)
        }
    }
}

fun DelegateExecution.readErrors(): Errors = getVariable(ERRORS_VARIABLE_NAME)
    ?.let { it as Errors }
    ?: Errors(emptyList())

fun DelegateExecution.writeErrors(value: Errors) = setVariable(ERRORS_VARIABLE_NAME, value)

fun DelegateExecution.writeIncident(value: Incident) = setVariable(INCIDENT_VARIABLE_NAME, value)
