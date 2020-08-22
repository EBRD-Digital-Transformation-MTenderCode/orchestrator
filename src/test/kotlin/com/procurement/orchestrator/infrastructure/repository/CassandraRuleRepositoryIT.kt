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
import com.procurement.orchestrator.application.model.Phase
import com.procurement.orchestrator.application.model.Stage
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.model.process.ProcessDefinitionKey
import com.procurement.orchestrator.application.repository.RuleRepository
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.model.ProcurementMethodDetails
import com.procurement.orchestrator.domain.model.address.country.CountryId
import com.procurement.orchestrator.extension.junit.assertFailure
import com.procurement.orchestrator.extension.junit.assertSuccess
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
class CassandraRuleRepositoryIT {
    companion object {
        private const val KEY_SPACE = "orchestrator"
        private const val TABLE_NAME = "rules"
        private const val COLUMN_COUNTRY = "country"
        private const val COLUMN_PMD = "pmd"
        private const val COLUMN_PROCESS_DEFINITION_KEY = "process_definition_key"
        private const val COLUMN_STAGE_FROM = "stage_from"
        private const val COLUMN_STAGE_TO = "stage_to"
        private const val COLUMN_PHASE_FROM = "phase_from"
        private const val COLUMN_PHASE_TO = "phase_to"
        private const val COLUMN_OPERATION_TYPE = "operation_type"

        private const val COUNTRY_ID: String = "MD"
        private val PMD = ProcurementMethodDetails.DA
        private val PROCESS_DEFINITION_KEY: ProcessDefinitionKey = ProcessDefinitionKey("Process-key-1")
        private val STAGE_PREV: Stage = Stage.EV
        private val STAGE_NEW: Stage = Stage.AC
        private val PHASE_PREV: Phase = Phase("Phase-1")
        private val PHASE_NEW: Phase = Phase("Phase-2")
        private val OPERATION_TYPE: OperationTypeProcess = OperationTypeProcess.TENDER_CANCELLATION
    }

    @Autowired
    private lateinit var container: CassandraTestContainer

    private lateinit var session: Session
    private lateinit var repository: RuleRepository

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

        repository = CassandraRuleRepository(session)
    }

    @AfterEach
    fun clean() {
        dropKeyspace()
    }

    @Test
    fun getProcessDefinitionKey() {
        insertProcess(processDefinitionKey = PROCESS_DEFINITION_KEY)

        val actualRules = assertSuccess {
            repository.load(countryId = COUNTRY_ID, pmd = PMD, processDefinitionKey = PROCESS_DEFINITION_KEY)
        }


        assertEquals(1, actualRules.size)
        assertEquals(COUNTRY_ID, actualRules[0].countryId)
        assertEquals(PMD, actualRules[0].pmd)
        assertEquals(PROCESS_DEFINITION_KEY, actualRules[0].processDefinitionKey)
        assertEquals(STAGE_PREV, actualRules[0].stageFrom)
        assertEquals(STAGE_NEW, actualRules[0].stageTo)
        assertEquals(PHASE_PREV, actualRules[0].phaseFrom)
        assertEquals(PHASE_NEW, actualRules[0].phaseTo)
        assertEquals(OPERATION_TYPE, actualRules[0].operationType)
    }

    @Test
    fun processDefinitionKeyNotFound() {
        val actualRules = assertSuccess {
            repository.load(countryId = COUNTRY_ID, pmd = PMD, processDefinitionKey = PROCESS_DEFINITION_KEY)
        }

        assertTrue(actualRules.isEmpty())
    }

    @Test
    fun errorSavingProcessDefinitionKey() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val fail: Fail.Incident.Database.Access = assertFailure {
            repository.load(countryId = COUNTRY_ID, pmd = PMD, processDefinitionKey = PROCESS_DEFINITION_KEY)
        }

        assertEquals("Error accessing to database.", fail.description)
    }

    private fun insertProcess(
        countryId: CountryId = COUNTRY_ID,
        pmd: ProcurementMethodDetails = PMD,
        processDefinitionKey: ProcessDefinitionKey = PROCESS_DEFINITION_KEY,
        stageFrom: Stage = STAGE_PREV,
        stageTo: Stage = STAGE_NEW,
        phaseFrom: Phase = PHASE_PREV,
        phaseTo: Phase = PHASE_NEW,
        operationType: OperationTypeProcess = OPERATION_TYPE
    ) {
        val insert = QueryBuilder.insertInto(KEY_SPACE, TABLE_NAME)
            .value(COLUMN_COUNTRY, countryId)
            .value(COLUMN_PMD, pmd.name)
            .value(COLUMN_PROCESS_DEFINITION_KEY, processDefinitionKey.toString())
            .value(COLUMN_STAGE_FROM, stageFrom.toString())
            .value(COLUMN_STAGE_TO, stageTo.toString())
            .value(COLUMN_PHASE_FROM, phaseFrom.toString())
            .value(COLUMN_PHASE_TO, phaseTo.toString())
            .value(COLUMN_OPERATION_TYPE, operationType.key)

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
                  process_definition_key TEXT,
                  stage_from             TEXT,
                  phase_from             TEXT,
                  stage_to               TEXT,
                  phase_to               TEXT,
                  operation_type         TEXT,
                  PRIMARY KEY ((country, pmd, process_definition_key), stage_from, phase_from)
                );
            """
        )
    }
}
