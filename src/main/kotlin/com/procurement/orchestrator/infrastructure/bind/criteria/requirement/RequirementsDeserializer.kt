package com.procurement.orchestrator.infrastructure.bind.criteria.requirement

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.document.DocumentReference
import com.procurement.orchestrator.domain.model.requirement.RequirementStatus
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.ExpectedValue
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.MaxValue
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.MinValue
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.NoneValue
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RangeValue
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.Requirement
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementDataType
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementValue
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.Requirements
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.eligible.evidence.EligibleEvidence
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.eligible.evidence.EligibleEvidenceType
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.eligible.evidence.EligibleEvidences
import com.procurement.orchestrator.exceptions.ErrorException
import com.procurement.orchestrator.exceptions.ErrorType
import com.procurement.orchestrator.infrastructure.bind.date.JsonDateTimeDeserializer
import java.io.IOException
import java.math.BigDecimal
import java.time.LocalDateTime

class RequirementsDeserializer : JsonDeserializer<Requirements>() {
    companion object {
        fun deserialize(requirements: ArrayNode): List<Requirement> {

            return requirements.map { requirement ->
                val id: String = requirement.get("id").asText()
                val title: String = requirement.get("title").asText()
                val description: String? = requirement.takeIf { it.has("description") }?.get("description")?.asText()
                val status: RequirementStatus? = requirement.takeIf { it.has("status") }
                    ?.let { RequirementStatus.orThrow(requirement.get("status").asText()) }

                val datePublished: LocalDateTime? = requirement
                    .takeIf { it.has("datePublished") }
                    ?.let { dateNode -> JsonDateTimeDeserializer.deserialize(dateNode.get("datePublished").asText()) }

                val dataType: RequirementDataType? = requirement.takeIf { it.has("dataType") }
                    ?.let { RequirementDataType.orThrow(requirement.get("dataType").asText()) }

                val period: Requirement.Period? = requirement.takeIf { it.has("period") }
                    ?.let {
                        val period = it.get("period")
                        val startDate = JsonDateTimeDeserializer.deserialize(period.get("startDate").asText())
                        val endDate = JsonDateTimeDeserializer.deserialize(period.get("endDate").asText())
                        Requirement.Period(
                            startDate = startDate,
                            endDate = endDate
                        )
                    }
                val eligibleEvidencesNode = requirement.get("eligibleEvidences")?.let { it as ArrayNode }

                val eligibleEvidences: EligibleEvidences = eligibleEvidencesNode
                    ?.map {
                        EligibleEvidence(
                            id = it.get("id").asText(),
                            title = it.get("title").asText(),
                            description = it.takeIf { it.has("description") }?.get("description")?.asText(),
                            type = EligibleEvidenceType.orThrow(it.get("type").asText()),
                            relatedDocument = it.takeIf { it.has("relatedDocument") }
                                ?.get("relatedDocument")
                                ?.get("id")
                                ?.let {relatedDocument -> DocumentId.create(relatedDocument.asText()) }
                                ?.let {relatedDocument -> DocumentReference(relatedDocument) }
                        )
                    }
                    .orEmpty()
                    .let { EligibleEvidences(it) }

                Requirement(
                    id = id,
                    title = title,
                    description = description,
                    period = period,
                    dataType = dataType,
                    status = status,
                    datePublished = datePublished,
                    value = requirementValue(requirement),
                    eligibleEvidences = eligibleEvidences
                )
            }
        }

        private fun requirementValue(requirementNode: JsonNode): RequirementValue {
            fun datatypeMismatchException(): Nothing = throw ErrorException(
                ErrorType.INVALID_REQUIREMENT_VALUE,
                message = "Requirement.dataType mismatch with datatype in expectedValue || minValue || maxValue."
            )
            fun datatypeMissingException(): Nothing = throw ErrorException(
                ErrorType.INVALID_REQUIREMENT_VALUE,
                message = "Missing 'requirement.dataType'."
            )

            val dataType = requirementNode.get("dataType")?.let { RequirementDataType.orThrow(it.asText())  }
            return when {
                isExpectedValue(requirementNode) -> {
                    when (dataType) {
                        RequirementDataType.BOOLEAN ->
                            if (requirementNode.get("expectedValue").isBoolean)
                                ExpectedValue.of(requirementNode.get("expectedValue").booleanValue())
                            else
                                datatypeMismatchException()

                        RequirementDataType.STRING ->
                            if (requirementNode.get("expectedValue").isTextual)
                                ExpectedValue.of(requirementNode.get("expectedValue").textValue())
                            else
                                datatypeMismatchException()

                        RequirementDataType.NUMBER ->
                            if (requirementNode.get("expectedValue").isBigDecimal
                                || requirementNode.get("expectedValue").isBigInteger
                            ) {
                                ExpectedValue.of(BigDecimal(requirementNode.get("expectedValue").asText()))
                            } else
                                datatypeMismatchException()

                        RequirementDataType.INTEGER ->
                            if (requirementNode.get("expectedValue").isBigInteger)
                                ExpectedValue.of(requirementNode.get("expectedValue").longValue())
                            else
                                datatypeMismatchException()

                        null -> datatypeMissingException()
                    }
                }
                isRange(requirementNode) -> {
                    when (dataType) {
                        RequirementDataType.NUMBER ->
                            if ((requirementNode.get("minValue").isBigDecimal || requirementNode.get("minValue").isBigInteger)
                                && (requirementNode.get("maxValue").isBigDecimal || requirementNode.get("maxValue").isBigInteger)
                            )
                                RangeValue.of(
                                    BigDecimal(requirementNode.get("minValue").asText()),
                                    BigDecimal(requirementNode.get("maxValue").asText())
                                )
                            else
                                datatypeMismatchException()

                        RequirementDataType.INTEGER ->
                            if (requirementNode.get("minValue").isBigInteger
                                && requirementNode.get("maxValue").isBigInteger
                            ) {
                                RangeValue.of(
                                    requirementNode.get("minValue").asLong(),
                                    requirementNode.get("maxValue").asLong()
                                )
                            } else
                                datatypeMismatchException()

                        RequirementDataType.BOOLEAN,
                        RequirementDataType.STRING ->
                            throw ErrorException(
                                ErrorType.INVALID_REQUIREMENT_VALUE,
                                message = "Boolean or String datatype cannot have a range"
                            )

                        null -> datatypeMissingException()
                    }
                }
                isOnlyMax(requirementNode) -> {
                    when (dataType) {
                        RequirementDataType.NUMBER ->
                            if (requirementNode.get("maxValue").isBigDecimal || requirementNode.get("maxValue").isBigInteger)
                                MaxValue.of(BigDecimal(requirementNode.get("maxValue").asText()))
                            else
                                datatypeMismatchException()
                        RequirementDataType.INTEGER ->
                            if (requirementNode.get("maxValue").isBigInteger)
                                MaxValue.of(requirementNode.get("maxValue").longValue())
                            else
                                datatypeMismatchException()
                        RequirementDataType.BOOLEAN,
                        RequirementDataType.STRING ->
                            throw ErrorException(
                                ErrorType.INVALID_REQUIREMENT_VALUE,
                                message = "Boolean or String datatype cannot have a max value"
                            )

                        null -> datatypeMissingException()
                    }
                }
                isOnlyMin(requirementNode) -> {
                    when (dataType) {
                        RequirementDataType.NUMBER ->
                            if (requirementNode.get("minValue").isBigDecimal || requirementNode.get("minValue").isBigInteger)
                                MinValue.of(BigDecimal(requirementNode.get("minValue").asText()))
                            else
                                datatypeMismatchException()

                        RequirementDataType.INTEGER ->
                            if (requirementNode.get("minValue").isBigInteger)
                                MinValue.of(requirementNode.get("minValue").longValue())
                            else
                                datatypeMismatchException()

                        RequirementDataType.BOOLEAN,
                        RequirementDataType.STRING ->
                            throw ErrorException(
                                ErrorType.INVALID_REQUIREMENT_VALUE,
                                message = "Boolean or String datatype cannot have a min value"
                            )

                        null -> datatypeMissingException()
                    }
                }
                isNotBounded(requirementNode) -> {
                    NoneValue
                }
                else -> {
                    throw ErrorException(
                        ErrorType.INVALID_REQUIREMENT_VALUE,
                        message = "Unknown value in Requirement object"
                    )
                }
            }
        }

        private fun isExpectedValue(requirementNode: JsonNode) = requirementNode.has("expectedValue")
            && !requirementNode.has("minValue") && !requirementNode.has("maxValue")

        private fun isRange(requirementNode: JsonNode) = requirementNode.has("minValue")
            && requirementNode.has("maxValue") && !requirementNode.has("expectedValue")

        private fun isOnlyMax(requirementNode: JsonNode) = requirementNode.has("maxValue")
            && !requirementNode.has("minValue") && !requirementNode.has("expectedValue")

        private fun isOnlyMin(requirementNode: JsonNode) = requirementNode.has("minValue")
            && !requirementNode.has("maxValue") && !requirementNode.has("expectedValue")

        private fun isNotBounded(requirementNode: JsonNode) = !requirementNode.has("expectedValue")
            && !requirementNode.has("minValue") && !requirementNode.has("maxValue")
    }

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(
        jsonParser: JsonParser,
        deserializationContext: DeserializationContext
    ): Requirements {
        val requirementNode = jsonParser.readValueAsTree<ArrayNode>()
        return Requirements(deserialize(requirementNode))
    }
}
