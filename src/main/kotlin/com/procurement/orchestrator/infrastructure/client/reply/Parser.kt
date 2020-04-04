package com.procurement.orchestrator.infrastructure.client.reply

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
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
import com.procurement.orchestrator.infrastructure.extension.jackson.tryGetAttributeAsEnum
import com.procurement.orchestrator.infrastructure.extension.jackson.tryGetTextAttribute

fun <R> String.parse(transform: Transform, target: Target<R>? = null): Result<Reply<R>, Fail.Incident> {

    val response = this

    val node: JsonNode = transform.tryParse(this)
        .orReturnFail { return failure(it) }

    val id = node.getId()
        .orReturnFail { fail -> return badResponse(error = fail, body = response) }

    val version = node.getVersion()
        .orReturnFail { fail -> return badResponse(error = fail, body = response) }

    val status = node.getStatus()
        .orReturnFail { fail -> return badResponse(error = fail, body = response) }

    return when (status) {
        Reply.Status.SUCCESS -> {
            val resultNode = node.getResultNodeOrNull()
            if (resultNode != null && target != null) {
                val result: Option<R> = transform.tryMapping(value = resultNode, target = target.typeRef)
                    .orReturnFail { fail -> return badResponse(error = fail, body = response) }
                    .asOption()
                Reply.Success(id = id, version = version, result = result)
            } else
                Reply.Success(id = id, version = version, result = Option.none())
        }

        Reply.Status.ERROR -> {
            val resultNode = node.getResultNode()
                .orReturnFail { fail -> return badResponse(error = fail, body = response) }
            val result = resultNode.parseErrors(transform = transform)
                .orReturnFail { fail -> return badResponse(error = fail, body = response) }
            Reply.Errors(id = id, version = version, result = result)
        }

        Reply.Status.INCIDENT -> {
            val resultNode = node.getResultNode()
                .orReturnFail { fail -> return badResponse(error = fail, body = response) }
            val result = resultNode.parseIncident(response = this, transform = transform)
                .orReturnFail { return failure(it) }
            Reply.Incident(id = id, version = version, result = result)
        }
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

private fun JsonNode.getVersion(): Result<String, DataValidationErrors> = this.tryGetTextAttribute("version")

private fun JsonNode.getStatus(): Result<Reply.Status, DataValidationErrors> =
    this.tryGetAttributeAsEnum(name = "status", enumProvider = Reply.Status)

private fun JsonNode.getResultNodeOrNull(): JsonNode? =
    this.getOrNull(name = "result")

private fun JsonNode.getResultNode(): Result<JsonNode, DataValidationErrors> =
    this.tryGetAttribute(name = "result")

private fun JsonNode.parseErrors(transform: Transform): Result<Reply.Errors.Result, Fail.Incident.Transform.Mapping> =
    transform.tryMapping(value = this, target = Response.Errors::class.java)
        .bind { errors ->
            errors
                .map { error ->
                    Reply.Errors.Result.Error(
                        code = error.code,
                        description = error.description,
                        details = error.details
                            .map { detail ->
                                Reply.Errors.Result.Error.Detail(id = detail.id, name = detail.name)
                            }
                    )
                }
                .let { Reply.Errors.Result(it) }
                .asSuccess<Reply.Errors.Result, Fail.Incident.Transform.Mapping>()
        }

private fun JsonNode.parseIncident(
    response: String,
    transform: Transform
): Result<Reply.Incident.Result, Fail.Incident> {
    val incident = transform.tryMapping(value = this, target = Response.Incident::class.java)
        .orReturnFail { fail -> return badResponse(error = fail, body = response) }

    val level = getLevel(incident.level)
        .orReturnFail { fail -> return badResponse(error = fail, body = response) }

    val service = incident.service
        .let { service ->
            Reply.Incident.Result.Service(
                id = service.id,
                name = service.name,
                version = service.version
            )
        }

    val details = incident.details
        .map { detail ->
            Reply.Incident.Result.Detail(
                code = detail.code,
                description = detail.description,
                metadata = detail.metadata
                    ?.let {
                        transform.tryToJson(it)
                            .orReturnFail { fail -> return failure(error = fail) }
                    }
                    ?: ""

            )
        }

    return Reply.Incident.Result(
        id = incident.id,
        date = incident.date,
        level = level,
        service = service,
        details = details
    ).asSuccess()
}

private fun getLevel(text: String): Result<Reply.Incident.Result.Level, DataValidationErrors.UnknownValue> =
    Reply.Incident.Result.Level.orNull(text)
        ?.asSuccess()
        ?: failure(
            DataValidationErrors.UnknownValue(
                name = "level",
                expectedValues = Reply.Incident.Result.Level.allowedElements.keysAsStrings(),
                actualValue = text
            )
        )

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
