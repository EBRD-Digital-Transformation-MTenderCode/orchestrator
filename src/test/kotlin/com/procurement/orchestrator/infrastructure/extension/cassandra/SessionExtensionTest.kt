package com.procurement.orchestrator.infrastructure.extension.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Session
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.whenever
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.extension.junit.assertFailure
import com.procurement.orchestrator.extension.junit.assertSuccess
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SessionExtensionTest {
    private val statement: BoundStatement = mock()
    private val session: Session = mock()

    @BeforeEach
    fun init() {
        reset(session)
    }

    @Nested
    inner class TryExecute {
        @Test
        fun success() {
            val resultSet: ResultSet = mock()
            whenever(session.execute(eq(statement)))
                .thenReturn(resultSet)

            val actualResultSet = assertSuccess {
                statement.tryExecute(session)
            }

            Assertions.assertEquals(resultSet, actualResultSet)
        }

        @Test
        fun failure() {
            doThrow(RuntimeException())
                .whenever(session)
                .execute(any<BoundStatement>())

            val fail: Fail.Incident.Database.Access = assertFailure {
                statement.tryExecute(session)
            }
            Assertions.assertEquals("Error accessing to database.", fail.description)
        }
    }
}
