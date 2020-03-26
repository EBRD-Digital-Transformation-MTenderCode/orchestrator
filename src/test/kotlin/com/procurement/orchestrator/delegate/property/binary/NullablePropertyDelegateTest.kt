package com.procurement.orchestrator.delegate.property.binary

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.context.container.DefaultPropertyContainer
import com.procurement.orchestrator.application.model.context.container.PropertyContainer
import com.procurement.orchestrator.application.model.context.property.nullablePropertyDelegate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class NullablePropertyDelegateTest {
    companion object {
        private const val USER_ID = 1
        private const val USER_NAME = "John"
        private val USER =
            User(
                USER_ID,
                USER_NAME
            )
    }

    @Test
    fun test() {
        val propertyContainer =
            DefaultPropertyContainer()
        val context =
            Context(
                propertyContainer
            )
        val user: User? = context.user

        assertNull(user)
    }

    @Test
    fun test2() {
        val propertyContainer = DefaultPropertyContainer()
            .apply {
                this["user"] =
                    USER
            }

        val context =
            Context(
                propertyContainer
            )
        val user: User? = context.user

        assertNotNull(user)
        assertEquals(USER_ID, user!!.id)
        assertEquals(USER_NAME, user.name)
    }

    @Test
    fun test3() {
        val propertyContainer =
            DefaultPropertyContainer()
        val context =
            Context(
                propertyContainer
            )
        val user =
            User(
                id = USER_ID,
                name = USER_NAME
            )
        context.user = user

        val actual = propertyContainer["user"]

        assertNotNull(actual)
        assertEquals(USER, actual)
    }

    class Context(propertyContainer: PropertyContainer) {
        var user: User? by nullablePropertyDelegate(
            propertyContainer
        )
    }

    data class User(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: Int,
        @field:JsonProperty("name") @param:JsonProperty("name") val name: String
    )
}
