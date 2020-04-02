package com.procurement.orchestrator.infrastructure.client.reply

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.fail.error.DataValidationErrors
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.functional.bind
import com.procurement.orchestrator.infrastructure.client.response.Response
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.extension.jackson.getOrNull
import com.procurement.orchestrator.infrastructure.extension.jackson.tryGetAttribute
import com.procurement.orchestrator.infrastructure.extension.jackson.tryGetAttributeAsEnum
import com.procurement.orchestrator.infrastructure.extension.jackson.tryGetTextAttribute

fun <R> String.parse(transform: Transform, target: Target<R>): Result<Reply<R>, Fail.Incident> {

    val response = this

    val node: JsonNode = transform.tryParse(this)
        .orReturnFail { return failure(it) }

    val id = node.getId()
        .orReturnFail { fail -> return badResponse(error = fail, body = response) }

    val version = node.getVersion()
        .orReturnFail { fail -> return badResponse(error = fail, body = response) }

    val status = node.getStatus()
        .orReturnFail { fail -> return badResponse(error = fail, body = response) }

    val result: Reply.Result<R> = when (status) {
        Reply.Status.SUCCESS -> {
            val resultNode = node.getResultNodeOrNull()
            if (resultNode != null) {
                val value = transform.tryMapping(value = resultNode, target = target.typeRef)
                    .orReturnFail { fail -> return badResponse(error = fail, body = response) }
                Reply.Result.Success(value)
            } else {
                when (target) {
                    is Target.Plural -> Reply.Result.Success(target.defaultResult())
                    is Target.Single -> return badResponse(
                        description = "Missing required attribute 'result' in response.",
                        body = response
                    )
                }
            }
        }

        Reply.Status.ERROR -> {
            val resultNode = node.getResultNode()
                .orReturnFail { fail -> return badResponse(error = fail, body = response) }
            val errors = transform.tryMapping(value = resultNode, target = Response.Errors::class.java)
                .orReturnFail { fail -> return badResponse(error = fail, body = response) }
                .map { fail ->
                    Reply.Result.Errors.Error(
                        code = fail.code,
                        description = fail.description,
                        details = fail.details
                            .map { detail ->
                                Reply.Result.Errors.Error.Detail(id = detail.id, name = detail.name)
                            }
                    )
                }

            Reply.Result.Errors(values = errors)
        }

        Reply.Status.INCIDENT -> {
            val resultNode = node.getResultNode()
                .orReturnFail { fail -> return badResponse(error = fail, body = response) }
            resultNode.parseReplyResultAsIncident(response = this, transform = transform)
                .orReturnFail { return failure(it) }
        }
    }
    return Reply(id = id, version = version, status = status, result = result)
        .asSuccess()
}

fun String.parse(transform: Transform): Result<Reply<Unit>, Fail.Incident> {

    val response = this

    val node: JsonNode = transform.tryParse(this)
        .orReturnFail { return failure(it) }

    val id = node.getId()
        .orReturnFail { fail -> return badResponse(error = fail, body = response) }

    val version = node.getVersion()
        .orReturnFail { fail -> return badResponse(error = fail, body = response) }

    val status = node.getStatus()
        .orReturnFail { fail -> return badResponse(error = fail, body = response) }

    val result: Reply.Result<Unit> = when (status) {
        Reply.Status.SUCCESS -> Reply.Result.Success(Unit)

        Reply.Status.ERROR -> {
            val resultNode = node.getResultNode()
                .orReturnFail { fail -> return badResponse(error = fail, body = response) }
            val errors = transform.tryMapping(value = resultNode, target = Response.Errors::class.java)
                .orReturnFail { fail -> return badResponse(error = fail, body = response) }
                .map { fail ->
                    Reply.Result.Errors.Error(
                        code = fail.code,
                        description = fail.description,
                        details = fail.details
                            .map { detail ->
                                Reply.Result.Errors.Error.Detail(id = detail.id, name = detail.name)
                            }
                    )
                }

            Reply.Result.Errors(values = errors)
        }

        Reply.Status.INCIDENT -> {
            val resultNode = node.getResultNode()
                .orReturnFail { fail -> return badResponse(error = fail, body = response) }
            resultNode.parseReplyResultAsIncident(response = this, transform = transform)
                .orReturnFail { return failure(it) }
        }
    }
    return Reply(id = id, version = version, status = status, result = result)
        .asSuccess()
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

private fun JsonNode.parseReplyResultAsIncident(
    response: String,
    transform: Transform
): Result<Reply.Result.Incident, Fail.Incident> {
    val incident = transform.tryMapping(value = this, target = Response.Incident::class.java)
        .orReturnFail { fail -> return badResponse(error = fail, body = response) }

    val level = getLevel(incident.level)
        .orReturnFail { fail -> return badResponse(error = fail, body = response) }

    val details = incident.details
        .map { detail ->
            Reply.Result.Incident.Detail(
                code = detail.code,
                description = detail.description,
                metadata = transform.tryToJson(detail.metadata)
                    .orReturnFail { fail -> return failure(error = fail) }
            )
        }

    return Reply.Result.Incident(
        id = incident.id,
        date = incident.date,
        level = level,
        service = incident.service
            .let { service ->
                Reply.Result.Incident.Service(
                    id = service.id,
                    name = service.name,
                    version = service.version
                )
            },
        details = details

    )
        .asSuccess()
}

private fun getLevel(text: String): Result<Reply.Result.Incident.Level, DataValidationErrors.UnknownValue> =
    Reply.Result.Incident.Level.orNull(text)
        ?.asSuccess()
        ?: failure(
            DataValidationErrors.UnknownValue(
                name = "level",
                expectedValues = Reply.Result.Incident.Level.allowedElements.keysAsStrings(),
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

private fun badResponse(description: String, body: String) =
    failure(
        Fail.Incident.BadResponse(description = description, body = body)
    )
