package com.procurement.orchestrator.infrastructure.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.procurement.orchestrator.infrastructure.bind.date.JsonDateTimeDeserializer
import com.procurement.orchestrator.infrastructure.bind.jackson.configuration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class JsonTransformTest {
    companion object {

        private const val USER_1_ID = 1
        private const val USER_2_ID = 2

        private const val USER_1_NAME = "User-1"
        private const val USER_2_NAME = "User-2"

        private const val USER_1_BIRTH_DAY_TEXT = "1995-06-05T17:59:01Z"
        private const val USER_2_BIRTH_DAY_TEXT = "1987-10-15T10:09:05Z"

        private val USER_1_BIRTH_DAY = JsonDateTimeDeserializer.deserialize(USER_1_BIRTH_DAY_TEXT)
        private val USER_2_BIRTH_DAY = JsonDateTimeDeserializer.deserialize(USER_2_BIRTH_DAY_TEXT)

        private const val USER_1_JSON =
            """{"id":$USER_1_ID,"name":"$USER_1_NAME","birthDay":"$USER_1_BIRTH_DAY_TEXT"}"""
        private const val USER_2_JSON =
            """{"id":$USER_2_ID,"name":"$USER_2_NAME","birthDay":"$USER_2_BIRTH_DAY_TEXT"}"""
        private const val USERS_JSON = """[$USER_1_JSON,$USER_2_JSON]"""

        private val transform = JacksonJsonTransform(ObjectMapper().apply { configuration() })
    }

    @Nested
    inner class Parse {
        @Test
        fun toSingleObjectByClass() {
            val userJsonNode = transform.tryParse(USER_1_JSON)
                .get

            assertEquals(3, userJsonNode.size())
            assertEquals(USER_1_ID, userJsonNode.get("id").asInt())
            assertEquals(USER_1_NAME, userJsonNode.get("name").asText())
            assertEquals(USER_1_BIRTH_DAY_TEXT, userJsonNode.get("birthDay").asText())
        }
    }

    @Nested
    inner class Mapping {
        @Test
        fun toSingleObjectByClass() {
            val userJsonNode = transform.tryParse(USER_1_JSON)
                .get

            val actualUser = transform.tryMapping(userJsonNode, User::class.java)
                .get

            assertEquals(USER_1_ID, actualUser.id)
            assertEquals(USER_1_NAME, actualUser.name)
            assertEquals(USER_1_BIRTH_DAY, actualUser.birthDay)
        }

        @Test
        fun toSingleObjectByTypeRef() {
            val typeRef = jacksonTypeRef<User>()
            val userJsonNode = transform.tryParse(USER_1_JSON)
                .get

            val actualUser = transform.tryMapping(userJsonNode, typeRef)
                .get

            assertEquals(USER_1_ID, actualUser.id)
            assertEquals(USER_1_NAME, actualUser.name)
            assertEquals(USER_1_BIRTH_DAY, actualUser.birthDay)
        }

        @Test
        fun toArrayObjectsByTypeRef() {
            val typeRef = jacksonTypeRef<List<User>>()
            val userJsonNode = transform.tryParse(USERS_JSON)
                .get

            val actualUser = transform.tryMapping(userJsonNode, typeRef)
                .get

            assertEquals(USER_1_ID, actualUser[0].id)
            assertEquals(USER_1_NAME, actualUser[0].name)
            assertEquals(USER_1_BIRTH_DAY, actualUser[0].birthDay)

            assertEquals(USER_2_ID, actualUser[1].id)
            assertEquals(USER_2_NAME, actualUser[1].name)
            assertEquals(USER_2_BIRTH_DAY, actualUser[1].birthDay)
        }
    }

    @Nested
    inner class Deserialization {
        @Test
        fun toSingleObjectByClass() {
            val actualUser = transform.tryDeserialization(USER_1_JSON, User::class.java)
                .get

            assertEquals(USER_1_ID, actualUser.id)
            assertEquals(USER_1_NAME, actualUser.name)
            assertEquals(USER_1_BIRTH_DAY, actualUser.birthDay)
        }

        @Test
        fun toSingleObjectByTypeRef() {
            val typeRef = jacksonTypeRef<User>()

            val actualUser = transform.tryDeserialization(USER_1_JSON, typeRef)
                .get

            assertEquals(USER_1_ID, actualUser.id)
            assertEquals(USER_1_NAME, actualUser.name)
            assertEquals(USER_1_BIRTH_DAY, actualUser.birthDay)
        }

        @Test
        fun toArrayObjectsByTypeRef() {
            val typeRef = jacksonTypeRef<List<User>>()

            val actualUser = transform.tryDeserialization(USERS_JSON, typeRef)
                .get

            assertEquals(2, actualUser.size)

            assertEquals(USER_1_ID, actualUser[0].id)
            assertEquals(USER_1_NAME, actualUser[0].name)
            assertEquals(USER_1_BIRTH_DAY, actualUser[0].birthDay)

            assertEquals(USER_2_ID, actualUser[1].id)
            assertEquals(USER_2_NAME, actualUser[1].name)
            assertEquals(USER_2_BIRTH_DAY, actualUser[1].birthDay)
        }
    }

    @Nested
    inner class Serialization {
        @Test
        fun toSingleObject() {
            val user = User(id = USER_1_ID, name = USER_1_NAME, birthDay = USER_1_BIRTH_DAY)
            val actualJson = transform.trySerialization(user)
            assertEquals(USER_1_JSON, actualJson.get)
        }
    }

    @Test
    fun toJsonFromJsonNode() {
        val userJsonNode = transform.tryParse(USER_1_JSON)
            .get

        val actualJson = transform.tryToJson(userJsonNode)

        assertEquals(USER_1_JSON, actualJson.get)
    }

    data class User(val id: Int, val name: String, val birthDay: LocalDateTime)
}
