package com.procurement.orchestrator.infrastructure.bpms.delegate.parameter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StateParameterTest {

    @Nested
    inner class ParseState {

        @Test
        fun onlyStatus() {
            val result = StateParameter.parse("status=COMPLETE")

            assertNotNull(result.status)
            assertNull(result.statusDetails)
            assertEquals("COMPLETE", result.status)
        }

        @Test
        fun onlyStatusDetails() {
            val result = StateParameter.parse("statusDetails=EMPTY")

            assertNull(result.status)
            assertNotNull(result.statusDetails)
            assertEquals("EMPTY", result.statusDetails)
        }

        @Test
        fun statusAndStatusDetails() {
            val result = StateParameter.parse("status=COMPLETE,statusDetails=EMPTY")

            assertNotNull(result.status)
            assertNotNull(result.statusDetails)
            assertEquals("COMPLETE", result.status)
            assertEquals("EMPTY", result.statusDetails)
        }

        @Test
        fun statusDetailsAndStatus() {
            val result = StateParameter.parse("statusDetails=EMPTY,status=COMPLETE")

            assertNotNull(result.status)
            assertNotNull(result.statusDetails)
            assertEquals("COMPLETE", result.status)
            assertEquals("EMPTY", result.statusDetails)
        }
    }
}
