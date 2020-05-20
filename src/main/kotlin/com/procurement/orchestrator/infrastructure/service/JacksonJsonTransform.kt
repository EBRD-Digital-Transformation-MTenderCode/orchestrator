package com.procurement.orchestrator.infrastructure.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import java.io.IOException

class JacksonJsonTransform(private val mapper: ObjectMapper) :
    Transform {

    /**
     * Parsing
     */
    override fun tryParse(value: String): Result<JsonNode, Fail.Incident.Transform.Parsing> = try {
        success(mapper.readTree(value))
    } catch (expected: IOException) {
        failure(Fail.Incident.Transform.Parsing(description = "Error of parsing.", exception = expected))
    }

    /**
     * Mapping
     */
    override fun <R> tryMapping(value: JsonNode, target: Class<R>): Result<R, Fail.Incident.Transform.Mapping> =
        try {
            success(mapper.treeToValue(value, target))
        } catch (expected: Exception) {
            failure(Fail.Incident.Transform.Mapping(description = "Error of mapping.", exception = expected))
        }

    override fun <R> tryMapping(
        value: JsonNode,
        typeRef: TypeReference<R>
    ): Result<R, Fail.Incident.Transform.Mapping> = try {
        val parser = mapper.treeAsTokens(value)
        success(mapper.readValue(parser, typeRef))
    } catch (expected: Exception) {
        failure(Fail.Incident.Transform.Mapping(description = "Error of mapping.", exception = expected))
    }

    /**
     * Deserialization
     */
    override fun <R> tryDeserialization(
        value: String,
        target: Class<R>
    ): Result<R, Fail.Incident.Transform.Deserialization> = try {
        success(mapper.readValue(value, target))
    } catch (expected: Exception) {
        failure(
            Fail.Incident.Transform.Deserialization(description = "Error of deserialization.", exception = expected)
        )
    }

    override fun <R> tryDeserialization(
        value: String,
        typeRef: TypeReference<R>
    ): Result<R, Fail.Incident.Transform.Deserialization> = try {
        success(mapper.readValue(value, typeRef))
    } catch (expected: Exception) {
        failure(
            Fail.Incident.Transform.Deserialization(description = "Error of deserialization.", exception = expected)
        )
    }

    /**
     * Serialization
     */
    override fun <R> trySerialization(value: R): Result<String, Fail.Incident.Transform.Serialization> = try {
        success(mapper.writeValueAsString(value))
    } catch (expected: Exception) {
        failure(Fail.Incident.Transform.Serialization(description = "Error of serialization.", exception = expected))
    }

    /**
     * ???
     */
    override fun tryToJson(value: JsonNode): Result<String, Fail.Incident.Transform.Serialization> = try {
        success(mapper.writeValueAsString(value))
    } catch (expected: Exception) {
        failure(Fail.Incident.Transform.Serialization(description = "Error of serialization.", exception = expected))
    }
}
