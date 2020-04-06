package com.procurement.orchestrator.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeType

object JsonPathParser {

    fun parse(json: String): Map<String, String> {
        val nodes = json.toNode()
        val valueByPath = mutableMapOf<String, String>()
        parsing(nodes, "root", "$", valueByPath)
        return valueByPath
    }

    private fun parsing(node: JsonNode, name: String, prefix: String, valueByPath: MutableMap<String, String>) {
        when {
            node.isObject -> {
                for ((fieldName, currentNode) in node.fields()) {
                    parsing(currentNode, fieldName, "$prefix['$fieldName']", valueByPath)
                }
            }
            node.isArray -> {
                if (node.size() > 0) {
                    val innerNode = node[0]
                    when (innerNode.nodeType) {
                        JsonNodeType.ARRAY,
                        JsonNodeType.OBJECT -> node.forEachIndexed { index, jsonNode ->
                            parsing(jsonNode, name, "$prefix['$name'][$index]", valueByPath)
                        }

                        else -> {
                            for (index in 0 until node.size()) {
                                val path = "$prefix['$name'][$index]"
                                val value: String = parsePrimitive(node[index], path)
                                valueByPath[path] = value
                            }
                        }
                    }
                }
            }
            else -> {
                val path = "$prefix['$name']"
                val value: String = parsePrimitive(node, path)
                valueByPath[path] = value
            }
        }
    }

    private fun parsePrimitive(node: JsonNode, path: String): String {
        return when (node.nodeType) {
            JsonNodeType.BOOLEAN -> if (node.asBoolean()) "true" else "false"
            JsonNodeType.NUMBER -> node.numberValue().toString()
            JsonNodeType.STRING -> "\"${node.asText()}\""
            else -> throw IllegalArgumentException("Error of parsing. Invalid node type '${node.nodeType}' by path: '$path'.")
        }
    }
}
