package com.procurement.orchestrator.infrastructure.client

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.extension.junit.assertSuccess
import com.procurement.orchestrator.infrastructure.bind.jackson.configuration
import com.procurement.orchestrator.infrastructure.client.reply.ParserV1
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.reply.ReplyId
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import com.procurement.orchestrator.infrastructure.service.JacksonJsonTransform
import com.procurement.orchestrator.json.loadJson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ParserReplyV1Test {

    companion object {
        private val transform: Transform = JacksonJsonTransform(
            mapper = ObjectMapper().apply {
                configuration()
            }
        )

        private val TARGET: Target<Result> = Target.single()

        private val REPLY_ID = ReplyId.fromString("43edc9e9-f899-4843-8afc-e97d7af5d2e7")
        private val REPLY_VERSION = Version.parse("2.0.0")
        private const val USER_ID = 1
        private const val USER_NAME = "John"

        private const val ERROR_CODE = "VR-1"
        private const val ERROR_DESCRIPTION = "Error-1"

        val parser = ParserV1
    }

    @Nested
    inner class Success {

        @Test
        fun withData() {
            val expectedResult = Result(
                user = User(
                    id = USER_ID,
                    name = USER_NAME
                )
            )
            val json = loadJson("json/client/reply/v1/reply_success_with_data.json")
            val actualReply = assertSuccess {
                parser.parse(response = json, transform = transform, target = TARGET)
            }

            val success = actualReply as Reply.Success
            assertEquals(REPLY_ID, success.id)
            assertEquals(REPLY_VERSION, success.version)
            assertTrue(success.result.isDefined)
            assertEquals(expectedResult, success.result.get)
        }

        @Test
        fun withoutData() {
            val json = loadJson("json/client/reply/v1/reply_success_without_data.json")
            val actualReply = assertSuccess {
                parser.parse<Result>(response = json, transform = transform)
            }

            val success = actualReply as Reply.Success
            assertEquals(REPLY_ID, success.id)
            assertEquals(REPLY_VERSION, success.version)
            assertTrue(success.result.isEmpty)
        }

        @Test
        fun noVersion() {
            val expectedResult = Result(
                user = User(
                    id = USER_ID,
                    name = USER_NAME
                )
            )
            val json = loadJson("json/client/reply/v1/reply_success_no_version.json")
            val actualReply = assertSuccess {
                parser.parse(response = json, transform = transform, target = TARGET)
            }
            val defaultVersion = Version.tryCreateOrNull("0.0.0")
            val success = actualReply as Reply.Success
            assertEquals(REPLY_ID, success.id)
            assertEquals(defaultVersion, success.version)
            assertTrue(success.result.isDefined)
            assertEquals(expectedResult, success.result.get)
        }
    }

    @Nested
    inner class Errors {

        @Test
        fun parse() {
            val expectedReply = Reply.Errors(
                id = REPLY_ID,
                version = REPLY_VERSION,
                result = Reply.Errors.Result(
                    values = listOf(
                        Reply.Errors.Result.Error(
                            code = ERROR_CODE,
                            description = ERROR_DESCRIPTION,
                            details = emptyList()
                        )
                    )
                )
            )
            val json = loadJson("json/client/reply/v1/reply_errors.json")
            val actualReply = assertSuccess {
                parser.parse(response = json, transform = transform, target = TARGET)
            }

            val errors = actualReply as Reply.Errors
            assertEquals(expectedReply.id, errors.id)
            assertEquals(expectedReply.version, errors.version)
            assertTrue(errors.result.size == expectedReply.result.size)

            expectedReply.result
                .forEachIndexed { indexError, expectedError ->
                    val error = errors.result[indexError]
                    assertEquals(expectedError.code, error.code)
                    assertEquals(expectedError.description, error.description)
                    assertEquals(expectedError.details, error.details)

                }
        }
    }

    data class Result(
        @field:JsonProperty("user") @param:JsonProperty("user") val user: User
    )

    data class User(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: Int,
        @field:JsonProperty("name") @param:JsonProperty("name") val name: String
    )
}
