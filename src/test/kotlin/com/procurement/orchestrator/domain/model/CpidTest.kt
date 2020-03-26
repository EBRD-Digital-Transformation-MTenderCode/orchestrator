package com.procurement.orchestrator.domain.model

import com.procurement.orchestrator.domain.extension.date.nowDefaultUTC
import com.procurement.orchestrator.domain.extension.date.toMilliseconds
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class CpidTest {
    companion object {
        private const val PREFIX = "ocds-t1t2t3"
        private const val COUNTRY = "MD"
        private val TIMESTAMP = nowDefaultUTC()
        private val MILLISECONDS = TIMESTAMP.toMilliseconds()
        private val CPID_TEXT = "$PREFIX-$COUNTRY-$MILLISECONDS"
    }

    @Test
    fun generate() {
        val cpid = Cpid.generate(prefix = PREFIX, country = COUNTRY, timestamp = TIMESTAMP)
        assertEquals(CPID_TEXT, cpid.toString())
    }

    @Test
    fun tryCreateOrNull() {
        val cpid = Cpid.tryCreateOrNull(CPID_TEXT)
        assertNotNull(cpid)
        assertEquals(CPID_TEXT, cpid!!.toString())
    }
}
