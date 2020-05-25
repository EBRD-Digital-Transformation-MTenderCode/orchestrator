package com.procurement.orchestrator.infrastructure.bpms.delegate

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import org.camunda.bpm.engine.delegate.DelegateExecution

class ParameterContainer(private val execution: DelegateExecution) {

    fun getStringOrNull(name: String): Result<String?, Fail.Incident.Bpmn.Parameter.DataTypeMismatch> {
        val value: String? = execution.getVariable(name)
            ?.let {
                if (it !is String)
                    return failure(
                        Fail.Incident.Bpmn.Parameter.DataTypeMismatch(
                            name = name,
                            expectedType = String::class.qualifiedName!!,
                            actualType = it::class.qualifiedName!!
                        )
                    )
                it as String
            }
        return success(value)
    }

    fun getBooleanOrNull(name: String): Result<Boolean?, Fail.Incident.Bpmn.Parameter.DataTypeMismatch> {
        val value: Boolean? = getStringOrNull(name = name)
            .orForwardFail { fail -> return fail }
            ?.let {
                if (!it.isBoolean())
                    return failure(
                        Fail.Incident.Bpmn.Parameter.DataTypeMismatch(
                            name = name,
                            expectedType = Boolean::class.qualifiedName!!,
                            actualType = it::class.qualifiedName!!
                        )
                    )
                it.toBoolean()
            }
        return success(value)
    }

    fun getString(name: String): Result<String, Fail.Incident.Bpmn.Parameter> {
        val value: String = execution.getVariable(name)
            ?.let {
                if (it !is String)
                    return failure(
                        Fail.Incident.Bpmn.Parameter.DataTypeMismatch(
                            name = name,
                            expectedType = String::class.qualifiedName!!,
                            actualType = it::class.qualifiedName!!
                        )
                    )
                it as String
            }
            ?: return failure(Fail.Incident.Bpmn.Parameter.MissingRequired(name = name))
        return success(value)
    }

    fun getListString(name: String): Result<List<String>, Fail.Incident.Bpmn.Parameter.DataTypeMismatch> {
        return execution.getVariable(name)
            ?.let { value ->
                if (value !is List<*>)
                    return failure(
                        Fail.Incident.Bpmn.Parameter.DataTypeMismatch(
                            name = name,
                            expectedType = List::class.qualifiedName!!,
                            actualType = value::class.qualifiedName!!
                        )
                    )
                success(value = value.map { item -> item!!.toString() })
            }
            ?: success(emptyList())
    }

    fun getMapString(name: String): Result<Map<String, String>, Fail.Incident.Bpmn.Parameter.DataTypeMismatch> {
        return execution.getVariable(name)
            ?.let { value ->
                if (value !is Map<*, *>)
                    return failure(
                        Fail.Incident.Bpmn.Parameter.DataTypeMismatch(
                            name = name,
                            expectedType = Map::class.qualifiedName!!,
                            actualType = value::class.qualifiedName!!
                        )
                    )
                success(value.map { (key, value) ->
                    key!!.toString() to value!!.toString()
                }.toMap())
            }
            ?: success(emptyMap())
    }

    private fun String.isBoolean(): Boolean = this == "true" || this == "false"
}
