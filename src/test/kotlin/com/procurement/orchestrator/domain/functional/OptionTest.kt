package com.procurement.orchestrator.domain.functional

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OptionTest {
    companion object {
        private const val VALUE = "Ok"
        private const val EXCEPTION_VALUE = "Error"
    }

    @Test
    fun pure() {
        val result = Option.pure(VALUE)

        assertTrue(result.isDefined)
        assertTrue(result.nonEmpty)
        assertEquals(VALUE, result.get)
    }

    @Test
    fun none() {
        val result = Option.none<String>()

        assertTrue(result.isEmpty)
        val exception = assertThrows<NoSuchElementException> {
            result.get
        }
        assertEquals("Option do not contain value.", exception.message)
    }

    @Test
    fun orNull() {
        val resultWithValue = Option.pure(VALUE)
        assertEquals(VALUE, resultWithValue.orNull)

        val resultWithoutValue = Option.none<String>()
        assertNull(resultWithoutValue.orNull)
    }

    @Test
    fun orThrow() {
        val expectedException = RuntimeException(EXCEPTION_VALUE)

        val resultWithValue = Option.pure(VALUE)
        assertEquals(VALUE, resultWithValue.orThrow { expectedException })

        val resultWithoutValue = Option.none<String>()
        val actualException = assertThrows<RuntimeException> {
            resultWithoutValue.orThrow { expectedException }
        }
        assertEquals(EXCEPTION_VALUE, actualException.message)
    }

    @Test
    fun fromNullable() {
        val resultWithValue = Option.fromNullable(VALUE)
        assertTrue(resultWithValue.isDefined)
        assertTrue(resultWithValue.nonEmpty)
        assertEquals(VALUE, resultWithValue.get)

        val resultWithoutValue = Option.fromNullable<String>(null)
        assertTrue(resultWithoutValue.isEmpty)
        val exception = assertThrows<NoSuchElementException> {
            resultWithoutValue.get
        }
        assertEquals("Option do not contain value.", exception.message)
    }

    @Test
    fun map() {
        val transform: (String) -> String = { it.toUpperCase() }

        val resultWithValue = Option.pure(VALUE.toLowerCase())
        val resultWithTransformedValue = resultWithValue.map(transform)

        assertTrue(resultWithTransformedValue.isDefined)
        assertTrue(resultWithTransformedValue.nonEmpty)
        assertEquals(VALUE.toUpperCase(), resultWithTransformedValue.get)
    }

    @Test
    fun bind() {
        val function: (String) -> Option<String> = { Option.pure(it.toUpperCase()) }

        val resultWithValue = Option.pure(VALUE.toLowerCase())
        val resultWithTransformedValue = resultWithValue.bind(function)

        assertTrue(resultWithTransformedValue.isDefined)
        assertTrue(resultWithTransformedValue.nonEmpty)
        assertEquals(VALUE.toUpperCase(), resultWithTransformedValue.get)
    }
}
