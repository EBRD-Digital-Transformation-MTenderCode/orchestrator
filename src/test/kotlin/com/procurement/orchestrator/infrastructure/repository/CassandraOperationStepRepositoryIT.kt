package com.procurement.orchestrator.infrastructure.repository

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.Cluster
import com.datastax.driver.core.HostDistance
import com.datastax.driver.core.PlainTextAuthProvider
import com.datastax.driver.core.PoolingOptions
import com.datastax.driver.core.Session
import com.datastax.driver.core.Statement
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.whenever
import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.process.ProcessId
import com.procurement.orchestrator.domain.extension.date.nowDefaultUTC
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.extension.junit.assertFailure
import com.procurement.orchestrator.extension.junit.assertSuccess
import com.procurement.orchestrator.infrastructure.bpms.model.TaskId
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStep
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.extension.cassandra.toCassandraTimestamp
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DatabaseTestConfiguration::class])
class CassandraOperationStepRepositoryIT {

    companion object {
        private const val KEY_SPACE = "orchestrator"
        private const val TABLE_NAME = "steps"
        private const val COLUMN_CPID = "cpid"
        private const val COLUMN_OPERATION_ID = "operation_id"
        private const val COLUMN_PROCESS_ID = "process_id"
        private const val COLUMN_TASK_ID = "task_id"
        private const val COLUMN_STEP_DATE = "step_date"
        private const val COLUMN_REQUEST = "request"
        private const val COLUMN_RESPONSE = "response"
        private const val COLUMN_CONTEXT = "context"

        private val CPID: Cpid = Cpid.generate(prefix = "ocds-t1t2t3", country = "MD", timestamp = nowDefaultUTC())
        private val OPERATION_ID: OperationId = OperationId.tryCreateOrNull("8025d29c-f0e0-4a31-92c3-1a4a65065fc0")!!
        private const val PROCESS_ID: ProcessId = "Process-1"
        private const val TASK_ID: TaskId = "Task-1"
        private val DATE: LocalDateTime = LocalDateTime.now()
        private const val REQUEST_DATA: String = """ {"ac": "canceled data"} """
        private const val RESPONSE_DATA: String = """ {"status": "updated"} """
        private const val CONTEXT: String = "CONTEXT"
    }

    @Autowired
    private lateinit var container: CassandraTestContainer

    private lateinit var session: Session
    private lateinit var repository: OperationStepRepository

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

        repository = CassandraOperationStepRepository(session)
    }

    @AfterEach
    fun clean() {
        dropKeyspace()
    }

    @Test
    fun savingStep() {
        val savingStep = operationStep()
        assertSuccess {
            repository.save(savingStep)
        }

        val step = load()

        assertEquals(savingStep, step)
    }

    @Test
    fun savingDuplicateStep() {
        val savingStep = operationStep()
        assertSuccess {
            repository.save(savingStep)
        }

        val step = load()
        assertEquals(savingStep, step)

        val dupSave = assertSuccess {
            repository.save(savingStep)
        }

        assertFalse(dupSave)
    }

    @Test
    fun errorSavingStep() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val savingStep = operationStep()

        val fail: Fail.Incident.Database.Access = assertFailure {
            repository.save(savingStep)
        }

        assertEquals("Error accessing to database.", fail.description)
    }

    private fun load(
        cpid: Cpid = CPID,
        operationId: OperationId = OPERATION_ID,
        processId: ProcessId = PROCESS_ID,
        taskId: TaskId = TASK_ID,
        stepDate: LocalDateTime = DATE
    ): OperationStep {
        val query: Statement = QueryBuilder.select()
            .column(COLUMN_REQUEST)
            .column(COLUMN_RESPONSE)
            .column(COLUMN_CONTEXT)
            .from(KEY_SPACE, TABLE_NAME)
            .where(QueryBuilder.eq(COLUMN_CPID, cpid.toString()))
            .and(QueryBuilder.eq(COLUMN_OPERATION_ID, operationId.toString()))
            .and(QueryBuilder.eq(COLUMN_PROCESS_ID, processId))
            .and(QueryBuilder.eq(COLUMN_TASK_ID, taskId))
            .and(QueryBuilder.eq(COLUMN_STEP_DATE, stepDate.toCassandraTimestamp()))

        return session.execute(query)
            .one()
            .let { row ->
                OperationStep(
                    cpid = cpid,
                    operationId = operationId,
                    processId = processId,
                    taskId = taskId,
                    stepDate = stepDate,
                    request = row.getString(COLUMN_REQUEST),
                    response = row.getString(COLUMN_RESPONSE),
                    context = row.getString(COLUMN_CONTEXT)
                )
            }
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
                  cpid         TEXT,
                  operation_id TEXT,
                  process_id   TEXT,
                  task_id      TEXT,
                  step_date    TIMESTAMP,
                  request      TEXT,
                  response     TEXT,
                  context      TEXT,
                  PRIMARY KEY (cpid, operation_id, process_id, task_id, step_date)
                );
            """
        )
    }

    private fun operationStep(
        cpid: Cpid = CPID,
        operationId: OperationId = OPERATION_ID,
        processId: ProcessId = PROCESS_ID,
        taskId: TaskId = TASK_ID,
        stepDate: LocalDateTime = DATE,
        request: String = REQUEST_DATA,
        response: String = RESPONSE_DATA,
        context: String = CONTEXT
    ) = OperationStep(
        cpid = cpid,
        operationId = operationId,
        processId = processId,
        taskId = taskId,
        stepDate = stepDate,
        request = request,
        response = response,
        context = context
    )
}
