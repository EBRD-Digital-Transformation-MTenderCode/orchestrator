package com.procurement.orchestrator.infrastructure.bind.criteria.requirement

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.ExpectedValue
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.MaxValue
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.MinValue
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.NoneValue
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RangeValue
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.Requirements
import com.procurement.orchestrator.infrastructure.bind.date.JsonDateTimeSerializer
import java.io.IOException
import java.math.BigDecimal

class RequirementsSerializer : JsonSerializer<Requirements>() {
    companion object {
        fun serialize(requirements: Requirements): ArrayNode {
            fun BigDecimal.jsonFormat() = BigDecimal("%.3f".format(this))
            val serializedRequirements = JsonNodeFactory.withExactBigDecimals(true).arrayNode()

            requirements.map { requirement ->
                val requirementNode = JsonNodeFactory.withExactBigDecimals(true).objectNode()

                requirementNode.put("id", requirement.id)
                requirementNode.put("title", requirement.title)

                requirement.dataType?.let { requirementNode.put("dataType", it.key) }

                requirement.description?.let { requirementNode.put("description", it) }
                requirement.period?.let {
                    requirementNode.putObject("period")
                        .put("startDate", JsonDateTimeSerializer.serialize(it.startDate))
                        .put("endDate", JsonDateTimeSerializer.serialize(it.endDate))
                }

                if (requirement.eligibleEvidences.isNotEmpty()) {
                    val eligibleEvidencesNode = requirementNode.putArray("eligibleEvidences")
                    requirement.eligibleEvidences.map {
                        val eligibleEvidenceNode = JsonNodeFactory.withExactBigDecimals(true).objectNode()

                        eligibleEvidenceNode.put("id", it.id)
                        eligibleEvidenceNode.put("title", it.title)
                        it.description?.let { description -> eligibleEvidenceNode.put("description", description) }
                        eligibleEvidenceNode.put("type", it.type.key)

                        it.relatedDocument?.let { relatedDocument ->
                            eligibleEvidenceNode.putObject("relatedDocument")
                                .put("id", relatedDocument.id.toString())
                        }

                        eligibleEvidencesNode.add(eligibleEvidenceNode)
                    }
                }

                when (requirement.value) {

                    is ExpectedValue.AsString -> {
                        requirementNode.put("expectedValue", requirement.value.value)
                    }
                    is ExpectedValue.AsBoolean -> {
                        requirementNode.put("expectedValue", requirement.value.value)
                    }
                    is ExpectedValue.AsNumber -> {
                        requirementNode.put("expectedValue", requirement.value.value.jsonFormat())
                    }
                    is ExpectedValue.AsInteger -> {
                        requirementNode.put("expectedValue", requirement.value.value)
                    }

                    is RangeValue.AsNumber -> {
                        requirementNode.put("minValue", requirement.value.minValue.jsonFormat())
                        requirementNode.put("maxValue", requirement.value.maxValue.jsonFormat())
                    }
                    is RangeValue.AsInteger -> {
                        requirementNode.put("minValue", requirement.value.minValue)
                        requirementNode.put("maxValue", requirement.value.maxValue)
                    }

                    is MinValue.AsNumber -> {
                        requirementNode.put("minValue", requirement.value.value.jsonFormat())
                    }
                    is MinValue.AsInteger -> {
                        requirementNode.put("minValue", requirement.value.value)
                    }

                    is MaxValue.AsNumber -> {
                        requirementNode.put("maxValue", requirement.value.value.jsonFormat())
                    }
                    is MaxValue.AsInteger -> {
                        requirementNode.put("maxValue", requirement.value.value)
                    }
                    is NoneValue -> Unit
                }

                requirementNode
            }.also { it.forEach { requirement -> serializedRequirements.add(requirement) } }

            return serializedRequirements
        }
    }

    @Throws(IOException::class, JsonProcessingException::class)
    override fun serialize(
        requirements: Requirements,
        jsonGenerator: JsonGenerator,
        provider: SerializerProvider
    ) {
        jsonGenerator.writeTree(serialize(requirements))
    }
}
