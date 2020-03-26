package com.procurement.orchestrator.infrastructure.repository

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.Cluster
import com.datastax.driver.core.HostDistance
import com.datastax.driver.core.PlainTextAuthProvider
import com.datastax.driver.core.PoolingOptions
import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.whenever
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.extension.junit.assertFailure
import com.procurement.orchestrator.extension.junit.assertSuccess
import com.procurement.orchestrator.infrastructure.bpms.repository.ErrorDescriptionRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DatabaseTestConfiguration::class])
class CassandraErrorDescriptionRepositoryIT {
    companion object {
        private const val KEY_SPACE = "orchestrator"
        private const val TABLE_NAME = "error_descriptions"
        private const val COLUMN_CODE = "code"
        private const val COLUMN_LANGUAGE = "language"
        private const val COLUMN_DESCRIPTION = "description"

        private const val ERROR_CODE: String = "BR-1"
        private const val LANGUAGE_EN = "EN"
        private const val LANGUAGE_RO = "RO"
        private const val DESCRIPTION_EN = "BR-1-EN"
        private const val DESCRIPTION_RO = "BR-1-RO"
    }

    @Autowired
    private lateinit var container: CassandraTestContainer

    private lateinit var session: Session
    private lateinit var repository: ErrorDescriptionRepository

    @BeforeEach
    fun init() {
        val poolingOptions = PoolingOptions()
            .setMaxConnectionsPerHost(HostDistance.LOCAL, 1)
        val cluster = Cluster.builder()
            .addContactPoints(container.contractPoint)
            .withPort(container.port)
            .withoutJMXReporting()
            .withPoolingOptions(poolingOptions)
            .withAuthProvider(PlainTextAuthProvider(container.username, container.password))
            .build()

        session = spy(cluster.connect())

        createKeyspace()
        createTable()

        repository = CassandraErrorDescriptionRepository(session)
    }

    @AfterEach
    fun clean() {
        dropKeyspace()
    }

    @Test
    fun getProcessDefinitionKey() {
        insert(code = ERROR_CODE, language = LANGUAGE_EN, description = DESCRIPTION_EN)
        insert(code = ERROR_CODE, language = LANGUAGE_RO, description = DESCRIPTION_RO)

        val result = assertSuccess {
            repository.load(codes = listOf(ERROR_CODE), language = LANGUAGE_EN)
        }

        assertEquals(1, result.size)
        assertEquals(ERROR_CODE, result[0].code)
        assertEquals(DESCRIPTION_EN, result[0].description)
    }

    @Test
    fun processDefinitionKeyNotFound() {
        val result = assertSuccess {
            repository.load(codes = listOf(ERROR_CODE), language = LANGUAGE_EN)
        }

        assertTrue(result.isEmpty())
    }

    @Test
    fun errorSavingProcessDefinitionKey() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val fail: Fail.Incident.Database.Access = assertFailure {
            repository.load(codes = listOf(ERROR_CODE), language = LANGUAGE_EN)
        }

        assertEquals("Error accessing to database.", fail.description)
    }

    private fun insert(code: String, language: String, description: String) {
        val insert = QueryBuilder.insertInto(KEY_SPACE, TABLE_NAME)
            .value(COLUMN_CODE, code)
            .value(COLUMN_LANGUAGE, language)
            .value(COLUMN_DESCRIPTION, description)

        session.execute(insert)
    }

    private fun createKeyspace() {
        session.execute("CREATE KEYSPACE $KEY_SPACE WITH replication = {'class' : 'SimpleStrategy', 'replication_factor' : 1};")
    }

    private fun dropKeyspace() {
        session.execute("DROP KEYSPACE $KEY_SPACE;")
    }

    private fun createTable() {
        session.execute(
            """
                CREATE TABLE IF NOT EXISTS $KEY_SPACE.$TABLE_NAME (
                  code        TEXT,
                  language    TEXT,
                  description TEXT,
                  PRIMARY KEY (code, language)
                );
            """
        )
    }
}
