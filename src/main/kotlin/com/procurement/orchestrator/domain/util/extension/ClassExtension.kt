package com.procurement.orchestrator.domain.util.extension

import kotlin.reflect.KClass

inline fun <reified T : Any> T.qualifiedName(): String = T::class.qualifiedName!!

val KClass<*>.qualifiedNameCovering: String
    get() = if (this.isCompanion)
        this.qualifiedName!!.substring(0, this.qualifiedName!!.lastIndexOf("."))
    else
        this.qualifiedName!!
