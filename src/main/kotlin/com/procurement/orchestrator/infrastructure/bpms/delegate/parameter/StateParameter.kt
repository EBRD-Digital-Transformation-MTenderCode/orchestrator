package com.procurement.orchestrator.infrastructure.bpms.delegate.parameter

class StateParameter private constructor(val status: String?, val statusDetails: String?) {

    companion object {
        private const val STATUS = "status"
        private const val STATUS_DETAILS = "statusDetails"

        private const val ANY_BEGIN = """(\s*[a-zA-Z]+\s*=\s*[a-zA-Z]+\s*,\s*)*"""
        private const val ANY_END = """(\s*,\s*[a-zA-Z]+\s*=\s*[a-zA-Z]+\s*)*"""

        private val patternStatus =
            """$ANY_BEGIN$STATUS\s*=\s*(?<$STATUS>\w+)$ANY_END""".toRegex()

        private val patternStatusDetails =
            """$ANY_BEGIN$STATUS_DETAILS\s*=\s*(?<$STATUS_DETAILS>\w+)$ANY_END""".toRegex()

        fun parse(text: String): StateParameter =
            StateParameter(
                status = patternStatus.matchEntire(
                    text
                )
                    ?.let { result ->
                        val groups = result.groups as MatchNamedGroupCollection
                        groups[STATUS]!!.value
                    },
                statusDetails = patternStatusDetails.matchEntire(
                    text
                )
                    ?.let { result ->
                        val groups = result.groups as MatchNamedGroupCollection
                        groups[STATUS_DETAILS]!!.value
                    }
            )
    }
}
