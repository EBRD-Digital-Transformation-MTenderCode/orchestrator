package com.procurement.orchestrator.domain.functional

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ResultTest {
    companion object {
        private val SUCCESS_VALUE = "Ok".toLowerCase()
        private val FAILURE_VALUE = "Failure".toLowerCase()

        private val IDENTITY: (String) -> String = { it }
        private val TRANSFORM: (String) -> String = { it.toUpperCase() }
        private val TRANSFORM_ERROR: (String) -> String = { it.toUpperCase() }
        private val BINDING: (String) -> Result<String, String> = { Result.success(it.toUpperCase()) }
    }

    @Test
    fun pure() {
        val pure: Result<String, String> = Result.pure(SUCCESS_VALUE)

        assertTrue(pure.isSuccess)
        assertFalse(pure.isFail)
        assertEquals(SUCCESS_VALUE, pure.get)
    }

    @Test
    fun success() {
        val success: Result<String, String> = Result.success(SUCCESS_VALUE)

        assertTrue(success.isSuccess)
        assertFalse(success.isFail)
        assertEquals(SUCCESS_VALUE, success.get)
    }

    @Test
    fun failure() {
        val failure: Result<String, String> = Result.failure(FAILURE_VALUE)

        assertFalse(failure.isSuccess)
        assertTrue(failure.isFail)
        assertEquals(FAILURE_VALUE, failure.error)
    }

    @Nested
    inner class Get {
        @Test
        fun success() {
            val success: Result<String, String> = Result.success(SUCCESS_VALUE)
            assertTrue(success.isSuccess)
            assertDoesNotThrow {
                assertEquals(SUCCESS_VALUE, success.get)
            }
        }

        @Test
        fun failure() {
            val failure: Result<String, String> = Result.failure(FAILURE_VALUE)
            assertTrue(failure.isFail)
            val actualException = assertThrows<NoSuchElementException> {
                failure.get
            }
            assertEquals("The result does not contain a value.", actualException.message)
        }
    }

    @Nested
    inner class Error {
        @Test
        fun success() {
            val success: Result<String, String> = Result.success(SUCCESS_VALUE)
            assertTrue(success.isSuccess)
            val actualException = assertThrows<NoSuchElementException> {
                success.error
            }
            assertEquals("The result does not contain an error.", actualException.message)
        }

        @Test
        fun failure() {
            val failure: Result<String, String> = Result.failure(FAILURE_VALUE)
            assertTrue(failure.isFail)
            assertDoesNotThrow {
                assertEquals(FAILURE_VALUE, failure.error)
            }
        }
    }

    @Nested
    inner class OrNull {
        @Test
        fun success() {
            val success: Result<String, String> = Result.success(SUCCESS_VALUE)
            assertEquals(SUCCESS_VALUE, success.orNull)
        }

        @Test
        fun failure() {
            val failure: Result<String, String> = Result.failure(FAILURE_VALUE)
            assertNull(failure.orNull)
        }
    }

    @Nested
    inner class AsOption {
        @Test
        fun success() {
            val success: Option<String> = Result.success(SUCCESS_VALUE).asOption
            assertTrue(success.isDefined)
            assertEquals(SUCCESS_VALUE, success.get)
        }

        @Test
        fun failure() {
            val failure: Option<String> = Result.failure(FAILURE_VALUE).asOption
            assertTrue(failure.isEmpty)
        }
    }

    @Nested
    inner class OrThrow {
        @Test
        fun success() {
            val pure: Result<String, String> = Result.pure(SUCCESS_VALUE)
            assertDoesNotThrow {
                assertEquals(SUCCESS_VALUE, pure.orThrow { RuntimeException() })
            }
        }

        @Test
        fun failure() {
            val failure: Result<String, String> = Result.failure(FAILURE_VALUE)
            val actualException = assertThrows<RuntimeException> {
                failure.orThrow { RuntimeException(it) }
            }
            assertEquals(FAILURE_VALUE, actualException.message)
        }
    }

    @Nested
    inner class Map {
        @Nested
        inner class Identity {
            @Test
            fun success() {
                val pure: Result<String, String> = Result.pure(SUCCESS_VALUE)
                val transformed = pure.map(IDENTITY)
                assertTrue(transformed.isSuccess)
                assertEquals(SUCCESS_VALUE, transformed.get)
            }

            @Test
            fun failure() {
                val failure: Result<String, String> = Result.failure(FAILURE_VALUE)
                val transformed = failure.map(IDENTITY)
                assertTrue(transformed.isFail)
                assertEquals(FAILURE_VALUE, transformed.error)
            }
        }

        @Nested
        inner class Transform {
            @Test
            fun success() {
                val successResult: Result<String, String> = Result.pure(SUCCESS_VALUE)
                val transformed = successResult.map(TRANSFORM)
                assertTrue(transformed.isSuccess)
                assertEquals(TRANSFORM(SUCCESS_VALUE), transformed.get)
            }

            @Test
            fun failure() {
                val failureResult: Result<String, String> = Result.failure(FAILURE_VALUE)
                val transformed = failureResult.map(TRANSFORM)
                assertTrue(transformed.isFail)
                assertEquals(failureResult.error, transformed.error)
            }
        }
    }

    @Nested
    inner class Bind {
        @Test
        fun success() {
            val pure: Result<String, String> = Result.pure(SUCCESS_VALUE)
            val transformed = pure.bind(BINDING)
            assertTrue(transformed.isSuccess)
            assertEquals(BINDING(SUCCESS_VALUE).get, transformed.get)
        }

        @Test
        fun failure() {
            val failure: Result<String, String> = Result.failure(FAILURE_VALUE)
            val transformed = failure.bind(BINDING)
            assertTrue(transformed.isFail)
            assertEquals(failure.error, transformed.error)
        }
    }

    @Nested
    inner class MapError {
        @Nested
        inner class Identity {
            @Test
            fun success() {
                val pure: Result<String, String> = Result.pure(SUCCESS_VALUE)
                val transformed = pure.mapError(IDENTITY)
                assertTrue(transformed.isSuccess)
                assertEquals(SUCCESS_VALUE, transformed.get)
            }

            @Test
            fun failure() {
                val failureResultIdentity: Result<String, String> = Result.failure(FAILURE_VALUE)
                val transformed = failureResultIdentity.mapError(IDENTITY)
                assertTrue(transformed.isFail)
                assertEquals(FAILURE_VALUE, transformed.error)
            }
        }

        @Nested
        inner class Transform {
            @Test
            fun success() {
                val pure: Result<String, String> = Result.pure(SUCCESS_VALUE)
                val transformed = pure.mapError(TRANSFORM_ERROR)
                assertTrue(transformed.isSuccess)
                assertEquals(pure.get, transformed.get)
            }

            @Test
            fun failure() {
                val failure: Result<String, String> = Result.failure(FAILURE_VALUE)
                val transformed = failure.mapError(TRANSFORM_ERROR)
                assertTrue(transformed.isFail)
                assertEquals(TRANSFORM_ERROR(FAILURE_VALUE), transformed.error)
            }
        }
    }
}
