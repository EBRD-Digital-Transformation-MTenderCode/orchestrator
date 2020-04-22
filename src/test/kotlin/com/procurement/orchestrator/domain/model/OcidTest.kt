package com.procurement.orchestrator.domain.model

import com.procurement.orchestrator.application.model.Stage
import com.procurement.orchestrator.domain.extension.date.nowDefaultUTC
import com.procurement.orchestrator.domain.extension.date.toMilliseconds
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class OcidTest {
    companion object {
        private const val PREFIX = "ocds-t1t2t3"
        private const val COUNTRY = "MD"
        private val TIMESTAMP_1 = nowDefaultUTC()
        private val TIMESTAMP_2 = TIMESTAMP_1.plusSeconds(30)
        private val MILLISECONDS_1 = TIMESTAMP_1.toMilliseconds()
        private val MILLISECONDS_2 = TIMESTAMP_2.toMilliseconds()
        private val CPID_TEXT = "$PREFIX-$COUNTRY-$MILLISECONDS_1"
    }

    @ParameterizedTest
    @EnumSource(value = Stage::class)
    fun generate(stage: Stage) {
        val cpid = Cpid.generate(prefix = PREFIX, country = COUNTRY, timestamp = TIMESTAMP_1)
        val ocid = Ocid.SingleStage.generate(cpid = cpid, stage = stage, timestamp = TIMESTAMP_2)

        assertEquals("$CPID_TEXT-${stage.key.toUpperCase()}-$MILLISECONDS_2", ocid.toString())
        assertEquals(stage, (ocid as Ocid.SingleStage).stage)
    }

    @ParameterizedTest
    @EnumSource(value = Stage::class)
    fun tryCreateOrNull(stage: Stage) {
        val expectedValue = "$CPID_TEXT-${stage.key.toUpperCase()}-$MILLISECONDS_2"
        val ocid = Ocid.SingleStage.tryCreateOrNull(expectedValue)

        assertNotNull(ocid)
        assertEquals(expectedValue, ocid!!.toString())
        assertEquals(stage, (ocid as Ocid.SingleStage).stage)
    }
}
