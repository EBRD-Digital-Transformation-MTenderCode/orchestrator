package com.procurement.orchestrator.delegate

import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.whenever
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.extension.junit.assertFailure
import com.procurement.orchestrator.extension.junit.assertSuccess
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ParameterContainerTest {

    companion object {
        private const val NAME_VARIABLE = "variable-1"
        private const val NAME_UNKNOWN_VARIABLE = "variable-2"

        private const val VALUE_STRING_VARIABLE = "Var1"
        private const val VALUE_INT_VARIABLE = 42

        private const val VALUE_ITEM_1_LIST = "Var1"
        private const val VALUE_ITEM_2_LIST = "Var2"
        private val VALUE_STRING_LIST_VARIABLE = listOf(VALUE_ITEM_1_LIST, VALUE_ITEM_2_LIST)

        private val delegateExecution: DelegateExecution = mock()
        val container = ParameterContainer(delegateExecution)
    }

    @BeforeEach
    fun init() {
        reset(delegateExecution)
    }

    @Nested
    inner class GetString {
        @Test
        fun knownVariable() {
            whenever(delegateExecution.getVariable(eq(NAME_VARIABLE)))
                .thenReturn(VALUE_STRING_VARIABLE)

            val valueOfKnownVariable = assertSuccess {
                container.getString(NAME_VARIABLE)
            }
            assertEquals(VALUE_STRING_VARIABLE, valueOfKnownVariable)
        }

        @Test
        fun knownVariableNoString() {
            whenever(delegateExecution.getVariable(eq(NAME_VARIABLE)))
                .thenReturn(VALUE_INT_VARIABLE)

            val fail: Fail.Incident.Bpmn.Parameter.DataTypeMismatch = assertFailure {
                container.getString(NAME_VARIABLE)
            }
            assertEquals(
                "Data type a property '$NAME_VARIABLE' is a mismatch. Expected data type: '${String::class.qualifiedName!!}', actual data type: '${VALUE_INT_VARIABLE::class.qualifiedName!!}'.",
                fail.description
            )
        }

        @Test
        fun unknownVariable() {
            val fail: Fail.Incident.Bpmn.Parameter.MissingRequired = assertFailure {
                container.getString(NAME_UNKNOWN_VARIABLE)
            }
            assertEquals("Missing required delegate property '$NAME_UNKNOWN_VARIABLE'.", fail.description)
        }
    }

    @Nested
    inner class GetStringOrNull {
        @Test
        fun knownVariable() {
            whenever(delegateExecution.getVariable(eq(NAME_VARIABLE)))
                .thenReturn(VALUE_STRING_VARIABLE)

            val valueOfKnownVariable = assertSuccess {
                container.getStringOrNull(NAME_VARIABLE)
            }
            assertNotNull(valueOfKnownVariable)
            assertEquals(VALUE_STRING_VARIABLE, valueOfKnownVariable)
        }

        @Test
        fun knownVariableNoString() {
            whenever(delegateExecution.getVariable(eq(NAME_VARIABLE)))
                .thenReturn(VALUE_INT_VARIABLE)

            val fail: Fail.Incident.Bpmn.Parameter.DataTypeMismatch = assertFailure {
                container.getStringOrNull(NAME_VARIABLE)
            }
            assertEquals(
                "Data type a property '$NAME_VARIABLE' is a mismatch. Expected data type: '${String::class.qualifiedName!!}', actual data type: '${VALUE_INT_VARIABLE::class.qualifiedName!!}'.",
                fail.description
            )
        }

        @Test
        fun unknownVariable() {
            val valueOfUnknownVariable = assertSuccess {
                container.getStringOrNull(NAME_UNKNOWN_VARIABLE)
            }
            assertNull(valueOfUnknownVariable)
        }
    }

    @Nested
    inner class GetListString {
        @Test
        fun knownVariable() {
            whenever(delegateExecution.getVariable(eq(NAME_VARIABLE)))
                .thenReturn(VALUE_STRING_LIST_VARIABLE)

            val valueOfKnownVariable = assertSuccess {
                container.getListString(NAME_VARIABLE)
            }
            assertEquals(2, valueOfKnownVariable.size)
            assertEquals(VALUE_ITEM_1_LIST, valueOfKnownVariable[0])
            assertEquals(VALUE_ITEM_2_LIST, valueOfKnownVariable[1])
        }

        @Test
        fun knownVariableNoList() {
            whenever(delegateExecution.getVariable(eq(NAME_VARIABLE)))
                .thenReturn(VALUE_INT_VARIABLE)

            val fail: Fail.Incident.Bpmn.Parameter.DataTypeMismatch = assertFailure {
                container.getListString(NAME_VARIABLE)
            }
            assertEquals(
                "Data type a property '$NAME_VARIABLE' is a mismatch. Expected data type: '${List::class.qualifiedName!!}', actual data type: '${VALUE_INT_VARIABLE::class.qualifiedName!!}'.",
                fail.description
            )
        }

        @Test
        fun unknownVariable() {
            val valueOfUnknownVariable = assertSuccess {
                container.getListString(NAME_UNKNOWN_VARIABLE)
            }
            assertTrue(valueOfUnknownVariable.isEmpty())
        }
    }
}
