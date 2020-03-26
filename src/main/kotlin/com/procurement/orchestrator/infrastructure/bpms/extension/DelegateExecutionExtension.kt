package com.procurement.orchestrator.infrastructure.bpms.extension

import com.procurement.orchestrator.application.model.context.container.PropertyContainer
import com.procurement.orchestrator.application.model.context.members.Errors
import org.camunda.bpm.engine.delegate.DelegateExecution

private const val UPDATE_GLOBAL_CONTEXT = "UGC"
private const val ERRORS_NAME_ATTRIBUTE = "errors"
private const val RESULT_NAME_ATTRIBUTE = "result"

val DelegateExecution.isUpdateGlobalContext: Boolean
    get() = getVariable(UPDATE_GLOBAL_CONTEXT)?.toString()?.toBoolean() ?: true

fun DelegateExecution.asPropertyContainer() = object : PropertyContainer {

    override fun get(name: String): Any? = this@asPropertyContainer.getVariable(name)

    override fun set(name: String, value: Any) {
        this@asPropertyContainer.setVariable(name, value)
    }
}

fun DelegateExecution.errors(): Errors = getVariable(ERRORS_NAME_ATTRIBUTE)
    ?.let { it as Errors }
    ?: Errors(emptyList())

fun DelegateExecution.errors(value: Errors) = setVariable(ERRORS_NAME_ATTRIBUTE, value)

fun <T> DelegateExecution.setResult(value: T) {
    if (value is Unit)
        this.setVariable(RESULT_NAME_ATTRIBUTE, null)
    else
        this.setVariable(RESULT_NAME_ATTRIBUTE, value)
}
