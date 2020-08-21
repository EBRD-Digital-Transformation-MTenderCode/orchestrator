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
import com.procurement.orchestrator.application.model.process.ProcessDefinitionKey
import com.procurement.orchestrator.application.repository.ProcessDefinitionRepository
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.model.ProcurementMethodDetails
import com.procurement.orchestrator.domain.model.address.country.CountryId
import com.procurement.orchestrator.extension.junit.assertFailure
import com.procurement.orchestrator.extension.junit.assertSuccess
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DatabaseTestConfiguration::class])
class CassandraProcessDefinitionRepositoryIT {
    companion object {
        private const val KEY_SPACE = "orchestrator"
        private const val TABLE_NAME = "process_definitions"
        private const val COLUMN_COUNTRY = "country"
        private const val COLUMN_PMD = "pmd"
        private const val COLUMN_PROCESS_NAME = "process_name"
        private const val COLUMN_PROCESS_DEFINITION_KEY = "process_definition_key"

        private const val COUNTRY_ID: String = "MD"
        private val PMD = ProcurementMethodDetails.DA
        private const val PROCESS_NAME: String = "Process-1"
        private val PROCESS_DEFINITION_KEY: ProcessDefinitionKey = ProcessDefinitionKey("Process-key-1")
    }

    @Autowired
    private lateinit var container: CassandraTestContainer

    private lateinit var session: Session
    private lateinit var repository: ProcessDefinitionRepository

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

        repository = CassandraProcessDefinitionRepository(session)
    }

    @AfterEach
    fun clean() {
        dropKeyspace()
    }

    @Test
    fun getProcessDefinitionKey() {
        insertProcess(processDefinitionKey = PROCESS_DEFINITION_KEY)

        val actualProcessDefinitionKey = assertSuccess {
            repository.getProcessDefinitionKey(countryId = COUNTRY_ID, pmd = PMD, processName = PROCESS_NAME)
        }

        assertEquals(PROCESS_DEFINITION_KEY, actualProcessDefinitionKey)
    }

    @Test
    fun processDefinitionKeyNotFound() {
        val actualProcessDefinitionKey = assertSuccess {
            repository.getProcessDefinitionKey(countryId = COUNTRY_ID, pmd = PMD, processName = PROCESS_NAME)
        }

        assertNull(actualProcessDefinitionKey)
    }

    @Test
    fun errorGetProcessDefinitionKey() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val fail: Fail.Incident.Database.Access = assertFailure {
            repository.getProcessDefinitionKey(countryId = COUNTRY_ID, pmd = PMD, processName = PROCESS_NAME)
        }

        assertEquals("Error accessing to database.", fail.description)
    }

    private fun insertProcess(
        countryId: CountryId = COUNTRY_ID,
        pmd: ProcurementMethodDetails = PMD,
        processName: String = PROCESS_NAME,
        processDefinitionKey: ProcessDefinitionKey = PROCESS_DEFINITION_KEY
    ) {
        val insert = QueryBuilder.insertInto(KEY_SPACE, TABLE_NAME)
            .value(COLUMN_COUNTRY, countryId)
            .value(COLUMN_PMD, pmd.name)
            .value(COLUMN_PROCESS_NAME, processName)
            .value(COLUMN_PROCESS_DEFINITION_KEY, processDefinitionKey.toString())

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
                  country                TEXT,
                  pmd                    TEXT,
                  process_name           TEXT,
                  process_definition_key TEXT,
                  PRIMARY KEY ((country, pmd, process_name))
                );
            """
        )
    }
}
