package com.procurement.orchestrator.infrastructure.client

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.extension.junit.assertFailure
import com.procurement.orchestrator.extension.junit.assertSuccess
import com.procurement.orchestrator.infrastructure.bind.jackson.configuration
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.reply.ReplyId
import com.procurement.orchestrator.infrastructure.client.reply.parse
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.service.JacksonJsonTransform
import com.procurement.orchestrator.json.loadJson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ParserReplyTest {

    companion object {
        private val transform: Transform = JacksonJsonTransform(
            mapper = ObjectMapper().apply {
                configuration()
            }
        )

        private val TARGET: Target<Result> = Target.single()

        private val REPLY_ID = ReplyId.fromString("43edc9e9-f899-4843-8afc-e97d7af5d2e7")
        private const val REPLY_VERSION = "2.0.0"
        private const val USER_ID = 1
        private const val USER_NAME = "John"

        private const val ERROR_CODE = "VR-1"
        private const val ERROR_DESCRIPTION = "Error-1"
        private const val ERROR_DETAILS_ID = "error-1.details.id"
        private const val ERROR_DETAILS_NAME = "error-1.details.name"

        private const val INCIDENT_ID = "1f8e227b-2486-45ff-92af-1a2a0290a1c5"
        private const val INCIDENT_DATE = "2020-04-04T13:49:57Z"
        private val INCIDENT_LEVEL = Reply.Incident.Result.Level.ERROR
        private const val INCIDENT_SERVICE_ID = "8e17c14f-71b2-4619-a484-e6934a36bd6b"
        private const val INCIDENT_SERVICE_NAME = "eTest"
        private const val INCIDENT_SERVICE_VERSION = "36bd6b"
        private const val INCIDENT_DETAILS_CODE = "incident.details.code"
        private const val INCIDENT_DETAILS_DESCRIPTION = "incident.details.name"
    }

    @Nested
    inner class Success {

        @Test
        fun withResult() {
            val expectedResult = Result(user = User(id = USER_ID, name = USER_NAME))
            val json = loadJson("json/client/reply/reply_success_with_result.json")
            val actualReply = assertSuccess {
                json.parse(transform = transform, target = TARGET)
            }

            val success = actualReply as Reply.Success
            assertEquals(REPLY_ID, success.id)
            assertEquals(REPLY_VERSION, success.version)
            assertTrue(success.result.isDefined)
            assertEquals(expectedResult, success.result.get)
        }

        @Test
        fun withoutResult() {
            val json = loadJson("json/client/reply/reply_success_without_result.json")
            val actualReply = assertSuccess {
                json.parse<Result>(transform = transform)
            }

            val success = actualReply as Reply.Success
            assertEquals(REPLY_ID, success.id)
            assertEquals(REPLY_VERSION, success.version)
            assertTrue(success.result.isEmpty)
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
                            details = listOf(
                                Reply.Errors.Result.Error.Detail(
                                    id = ERROR_DETAILS_ID,
                                    name = ERROR_DETAILS_NAME
                                )
                            )
                        )
                    )
                )
            )
            val json = loadJson("json/client/reply/reply_errors.json")
            val actualReply = assertSuccess {
                json.parse(transform = transform, target = TARGET)
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

                    expectedError.details
                        .forEachIndexed { indexDetails, expectedDetails ->
                            val detail = error.details[indexDetails]
                            assertEquals(expectedDetails.id, detail.id)
                            assertEquals(expectedDetails.name, detail.name)
                        }
                }
        }
    }

    @Nested
    inner class Incident {

        @Test
        fun parse() {
            val expectedReply = Reply.Incident(
                id = REPLY_ID,
                version = REPLY_VERSION,
                result = Reply.Incident.Result(
                    id = INCIDENT_ID,
                    date = INCIDENT_DATE,
                    level = INCIDENT_LEVEL,
                    service = Reply.Incident.Result.Service(
                        id = INCIDENT_SERVICE_ID,
                        name = INCIDENT_SERVICE_NAME,
                        version = INCIDENT_SERVICE_VERSION
                    ),
                    details = listOf(
                        Reply.Incident.Result.Detail(
                            code = INCIDENT_DETAILS_CODE,
                            description = INCIDENT_DETAILS_DESCRIPTION,
                            metadata = ""
                        )
                    )
                )
            )

            val json = loadJson("json/client/reply/reply_incident.json")
            val reply = assertSuccess {
                json.parse(transform = transform, target = TARGET)
            }

            val incident = reply as Reply.Incident
            assertEquals(expectedReply.id, incident.id)
            assertEquals(expectedReply.version, incident.version)

            assertEquals(expectedReply.result.id, incident.result.id)
            assertEquals(expectedReply.result.date, incident.result.date)
            assertEquals(expectedReply.result.level, incident.result.level)
            assertEquals(expectedReply.result.service.id, incident.result.service.id)
            assertEquals(expectedReply.result.service.name, incident.result.service.name)
            assertEquals(expectedReply.result.service.version, incident.result.service.version)

            expectedReply.result.details
                .forEachIndexed { indexDetails, expectedDetails ->
                    val detail = incident.result.details[indexDetails]
                    assertEquals(expectedDetails.code, detail.code)
                    assertEquals(expectedDetails.description, detail.description)
                    assertEquals(expectedDetails.metadata, detail.metadata)
                }
        }

        @Test
        fun invalidLevel() {

            val json = loadJson("json/client/reply/reply_incident_with_invalid_level.json")
            val incident: Fail.Incident = assertFailure {
                json.parse(transform = transform, target = TARGET)
            }

            assertEquals(
                "Attribute value mismatch of 'level' with one of enum expected values. Expected values: 'error, warning, info', actual value: 'unknown'.",
                incident.description
            )
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
