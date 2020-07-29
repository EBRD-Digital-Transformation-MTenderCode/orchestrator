package com.procurement.orchestrator.infrastructure.client.reply

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.fail.error.DataValidationErrors
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.asOption
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.functional.bind
import com.procurement.orchestrator.infrastructure.client.response.Response
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.extension.jackson.getOrNull
import com.procurement.orchestrator.infrastructure.extension.jackson.tryGetAttribute
import com.procurement.orchestrator.infrastructure.extension.jackson.tryGetTextAttribute
import com.procurement.orchestrator.infrastructure.model.Version

object ParserV1 {
    fun <R> parse(
        response: String,
        transform: Transform,
        target: Target<R>? = null
    ): Result<Reply<R>, Fail.Incident> {
        val node: JsonNode = transform.tryParse(response)
            .orForwardFail { fail -> return fail }

        val id = node.getId()
            .orReturnFail { fail -> return badResponse(error = fail, body = response) }

        val version = node.getResponseVersionOrDefault()
            .orReturnFail { fail -> return badResponse(error = fail, body = response) }

        val isSuccessResponse = node.isSuccessResponse()

        return if (isSuccessResponse) {
            val dataNode = node.getDataNodeOrNull()
            if (dataNode != null && target != null) {
                val data: Option<R> = transform.tryMapping(value = dataNode, target = target.typeRef)
                    .orReturnFail { fail -> return badResponse(error = fail, body = response) }
                    .asOption()
                Reply.Success(id = id, version = version, result = data)
            } else
                Reply.Success(id = id, version = version, result = Option.none())
        } else {
            val resultNode = node.getErrorsNode()
                .orReturnFail { fail -> return badResponse(error = fail, body = response) }
            val result = resultNode.parseErrors(transform = transform)
                .orReturnFail { fail -> return badResponse(error = fail, body = response) }
            Reply.Errors(id = id, version = version, result = result)
        }.asSuccess()
    }

    private fun JsonNode.getId(): Result<ReplyId, DataValidationErrors> = this.tryGetTextAttribute(name = "id")
        .bind {
            it.replyId()
                ?.asSuccess<ReplyId, DataValidationErrors>()
                ?: failure(
                    DataValidationErrors.DataFormatMismatch(name = "id", expectedFormat = "uuid", actualValue = it)
                )
        }

    private fun JsonNode.getResponseVersionOrDefault(): Result<Version, DataValidationErrors> {
        val defaultVersion: Version = Version.tryCreateOrNull("0.0.0")!!
        return this.tryGetTextAttribute("version")
            .orReturnFail { return defaultVersion.asSuccess() }
            .let { Version.tryCreateOrNull(it) ?: defaultVersion }
            .asSuccess()
    }

    private fun JsonNode.getDataNodeOrNull(): JsonNode? =
        this.getOrNull(name = "data")

    private fun JsonNode.getErrorsNode(): Result<JsonNode, DataValidationErrors> =
        this.tryGetAttribute(name = "errors")

    private fun JsonNode.parseErrors(transform: Transform): Result<Reply.Errors.Result, Fail.Incident.Transform.Mapping> =
        transform.tryMapping(value = this, target = Response.Errors::class.java)
            .bind { errors ->
                errors
                    .map { error ->
                        Reply.Errors.Result.Error(
                            code = error.code,
                            description = error.description,
                            details = emptyList()
                        )
                    }
                    .let { Reply.Errors.Result(it) }
                    .asSuccess<Reply.Errors.Result, Fail.Incident.Transform.Mapping>()
            }

    private fun badResponse(error: Fail.Incident.Transform.Mapping, body: String) =
        failure(
            Fail.Incident.BadResponse(
                description = error.description,
                exception = error.exception,
                body = body
            )
        )

    private fun badResponse(error: DataValidationErrors, body: String) =
        failure(
            Fail.Incident.BadResponse(description = error.description, body = body)
        )

    private fun JsonNode.isSuccessResponse(): Boolean = this.getErrorsNode().isFail
}


