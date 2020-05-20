package com.procurement.orchestrator.delegate.property.transform

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.procurement.orchestrator.application.model.context.container.DefaultPropertyContainer
import com.procurement.orchestrator.application.model.context.container.PropertyContainer
import com.procurement.orchestrator.application.model.context.property.collectionPropertyDelegate
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.infrastructure.service.JacksonJsonTransform
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class JsonCollectionPropertyDelegateTest {
    companion object {
        private const val USER_ID = 1
        private const val USER_NAME = "John"
        private const val JSON_USERS = """[{"id":$USER_ID,"name":"$USER_NAME"}]"""
    }

    private val transform = JacksonJsonTransform(ObjectMapper())

    @Test
    fun test() {
        val propertyContainer =
            DefaultPropertyContainer()
        val context = Context(transform, propertyContainer)
        val users: List<User> = context.users

        assertTrue(users.isEmpty())
    }

    @Test
    fun test2() {
        val propertyContainer = DefaultPropertyContainer()
            .apply { this["users"] = JSON_USERS }

        val context = Context(transform, propertyContainer)
        val users: List<User> = context.users

        assertTrue(users.isNotEmpty())
        assertTrue(users.size == 1)
        assertEquals(USER_ID, users[0].id)
        assertEquals(USER_NAME, users[0].name)
    }

    @Test
    fun test3() {
        val propertyContainer =
            DefaultPropertyContainer()
        val context = Context(transform, propertyContainer)
        val users = listOf(User(id = USER_ID, name = USER_NAME))
        context.users = users

        val actual = propertyContainer["users"]

        assertNotNull(actual)
        assertEquals(JSON_USERS, actual)
    }

    class Context(transform: Transform, propertyContainer: PropertyContainer) {
        var users: List<User> by collectionPropertyDelegate(transform, propertyContainer) { emptyList<User>() }
    }

    data class User(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: Int,
        @field:JsonProperty("name") @param:JsonProperty("name") val name: String
    )
}
