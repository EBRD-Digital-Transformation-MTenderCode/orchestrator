package com.procurement.orchestrator.infrastructure.bpms.model

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

class ResultContext : AbstractCoroutineContextElement(ResultContext) {
    companion object Key : CoroutineContext.Key<ResultContext>

    private val rqDefer = CompletableDeferred<String>()
    private val rsDefer = CompletableDeferred<String>()
    private val ctxDefer = CompletableDeferred<String>()

    fun request(value: String) {
        rqDefer.complete(value)
    }

    @ExperimentalCoroutinesApi
    fun request(): String = rqDefer.getCompleted()

    val hasRequest: Boolean
        get() = rqDefer.isCompleted

    fun response(value: String) {
        rsDefer.complete(value)
    }

    @ExperimentalCoroutinesApi
    fun response(): String = rsDefer.getCompleted()

    val hasResponse: Boolean
        get() = rsDefer.isCompleted

    fun globalContext(value: String) {
        ctxDefer.complete(value)
    }

    val hasGlobalContext: Boolean
        get() = ctxDefer.isCompleted

    @ExperimentalCoroutinesApi
    fun globalContext(): String = ctxDefer.getCompleted()
}
