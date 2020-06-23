package com.procurement.orchestrator.domain.model.item

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.io.Serializable
import java.util.*

class ItemId private constructor(private val value: String) : Serializable {

    companion object {

        @JvmStatic
        @JsonCreator
        fun create(text: String): ItemId = ItemId(text)

        fun generate(): ItemId = ItemId(UUID.randomUUID().toString())
    }

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is ItemId
                && this.value == other.value
        else
            true
    }

    override fun hashCode(): Int = value.hashCode()

    @JsonValue
    override fun toString(): String = value
}
