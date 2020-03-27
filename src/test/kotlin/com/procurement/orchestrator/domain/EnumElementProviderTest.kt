package com.procurement.orchestrator.domain

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.exceptions.EnumElementProviderException
import com.procurement.orchestrator.extension.junit.assertFailure
import com.procurement.orchestrator.extension.junit.assertSuccess
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class EnumElementProviderTest {

    @Test
    fun testAnnotations() {
        val allowedValuesExpected = "element-1 (Deprecated), element-3"
        val allowedValuesActual = ABC.allowedElements.keysAsStrings().joinToString()
        assertEquals(allowedValuesExpected, allowedValuesActual)
    }

    @Test
    fun orNull() {
        assertEquals(ABC.ELEMENT_1, ABC.orNull("element-1"))
        assertNull(ABC.orNull("element-4"))
    }

    @Test
    fun orThrow() {
        assertEquals(ABC.ELEMENT_1, ABC.orThrow("element-1"))

        assertThrows<EnumElementProviderException> {
            assertNull(ABC.orThrow("element-4"))
        }
    }

    @Test
    fun tryOf() {
        val element = assertSuccess {
            ABC.tryOf("element-1")
        }
        assertEquals(ABC.ELEMENT_1, element)

        val fail: String = assertFailure {
            ABC.tryOf("element-4")
        }
        assertTrue(fail.isNotBlank())
    }

    @Test
    fun contains() {
        assertTrue("element-1" in ABC)
        assertFalse("element-4" in ABC)
    }

    enum class ABC(@JsonValue override val key: String) : EnumElementProvider.Key {

        @EnumElementProvider.DeprecatedElement
        ELEMENT_1("element-1"),

        @EnumElementProvider.ExcludedElement
        ELEMENT_2("element-2"),

        ELEMENT_3("element-3");

        override fun toString(): String = key

        companion object : EnumElementProvider<ABC>(info = info())
    }
}

