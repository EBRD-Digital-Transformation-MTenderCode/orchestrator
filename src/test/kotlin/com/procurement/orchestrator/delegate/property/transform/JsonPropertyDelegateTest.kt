package com.procurement.orchestrator.delegate.property.transform

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.procurement.orchestrator.application.model.context.container.DefaultPropertyContainer
import com.procurement.orchestrator.application.model.context.container.PropertyContainer
import com.procurement.orchestrator.application.model.context.property.propertyDelegate
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.infrastructure.service.JacksonJsonTransform
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class JsonPropertyDelegateTest {
    companion object {
        private const val USER_ID = 1
        private const val USER_NAME = "John"
        private const val DEFAULT_USER_ID = 10
        private const val DEFAULT_USER_NAME = "David"

        private const val JSON_USER = """{"id":$USER_ID,"name":"$USER_NAME"}"""
    }

    private val transform = JacksonJsonTransform(ObjectMapper())

    @Test
    fun test() {
        val propertyContainer =
            DefaultPropertyContainer()
        val context = Context(transform, propertyContainer)
        val user = context.user

        assertEquals(DEFAULT_USER_ID, user.id)
        assertEquals(DEFAULT_USER_NAME, user.name)
    }

    @Test
    fun test2() {
        val propertyContainer = DefaultPropertyContainer()
            .apply { this["user"] = JSON_USER }

        val context = Context(transform, propertyContainer)
        val user = context.user

        assertEquals(USER_ID, user.id)
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

        Assertions.assertNotNull(actual)
        assertEquals(JSON_USER, actual)
    }

    class Context(transform: Transform, propertyContainer: PropertyContainer) {
        var user: User by propertyDelegate(transform, propertyContainer) {
            User(id = DEFAULT_USER_ID, name = DEFAULT_USER_NAME)
        }
    }

    data class User(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: Int,
        @field:JsonProperty("name") @param:JsonProperty("name") val name: String
    )
}
