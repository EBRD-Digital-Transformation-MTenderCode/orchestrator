package com.procurement.orchestrator.delegate.property.transform

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.procurement.orchestrator.application.model.context.container.DefaultPropertyContainer
import com.procurement.orchestrator.application.model.context.container.PropertyContainer
import com.procurement.orchestrator.application.model.context.property.nullablePropertyDelegate
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.infrastructure.service.JacksonJsonTransform
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class JsonNullablePropertyDelegateTest {
    companion object {
        private const val USER_ID = 1
        private const val USER_NAME = "John"
        private const val JSON_USER = """{"id":$USER_ID,"name":"$USER_NAME"}"""
    }

    private val transform = JacksonJsonTransform(ObjectMapper())

    @Test
    fun test() {
        val propertyContainer =
            DefaultPropertyContainer()
        val context = Context(transform, propertyContainer)
        val user: User? = context.user

        assertNull(user)
    }

    @Test
    fun test2() {
        val propertyContainer = DefaultPropertyContainer()
            .apply {
                this["user"] =
                    JSON_USER
            }

        val context = Context(transform, propertyContainer)
        val user: User? = context.user

        assertNotNull(user)
        assertEquals(USER_ID, user!!.id)
        assertEquals(USER_NAME, user.name)
    }

    @Test
    fun test3() {
        val propertyContainer =
            DefaultPropertyContainer()
        val context = Context(transform, propertyContainer)
        val user = User(id = USER_ID, name = USER_NAME)
        context.user = user

        val actual = propertyContainer["user"]

        assertNotNull(actual)
        assertEquals(JSON_USER, actual)
    }

    class Context(transform: Transform, propertyContainer: PropertyContainer) {
        var user: User? by nullablePropertyDelegate(transform, propertyContainer)
    }

    data class User(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: Int,
        @field:JsonProperty("name") @param:JsonProperty("name") val name: String
    )
}
