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
import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.Stage
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.extension.junit.assertFailure
import com.procurement.orchestrator.extension.junit.assertSuccess
import com.procurement.orchestrator.infrastructure.bpms.model.queue.QueueNoticeTask
import com.procurement.orchestrator.infrastructure.bpms.repository.NoticeQueueRepository
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
class CassandraNoticeQueueRepositoryIT {

    companion object {
        private const val KEY_SPACE = "orchestrator"
        private const val TABLE_NAME = "notice_queue"
        private const val COLUMN_OPERATION_ID = "operation_id"
        private const val COLUMN_TIMESTAMP = "timestamp"
        private const val COLUMN_CPID = "cpid"
        private const val COLUMN_OCID = "ocid"
        private const val COLUMN_ACTION_NAME = "action_name"
        private const val COLUMN_TASK_DATA = "data"

        private val OPERATION_ID: OperationId = OperationId.tryCreateOrNull("8025d29c-f0e0-4a31-92c3-1a4a65065fc0")!!
        private val TIMESTAMP: LocalDateTime = LocalDateTime.now()
        private val CPID: Cpid = Cpid.generate(prefix = "ocds-t1t2t3", country = "MD", timestamp = TIMESTAMP)
        private val OCID: Ocid = Ocid.generate(cpid = CPID, stage = Stage.AC, timestamp = TIMESTAMP)
        private val ACTION: QueueNoticeTask.Action = QueueNoticeTask.Action.UPDATE_RECORD
        private const val TASK_DATA: String = "Task Data"
    }

    @Autowired
    private lateinit var container: CassandraTestContainer

    private lateinit var session: Session
    private lateinit var repository: NoticeQueueRepository

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

        repository = CassandraNoticeQueueRepository(session)
    }

    @AfterEach
    fun clean() {
        dropKeyspace()
    }

    @Test
    fun savingTask() {
        val task = task()
        assertSuccess {
            repository.save(operationId = OPERATION_ID, task = task)
        }

        val tasks = assertSuccess {
            repository.load(operationId = OPERATION_ID)
        }

        assertEquals(1, tasks.size)
        assertEquals(task, tasks[0])
    }

    @Test
    fun savingDuplicateTask() {
        val task = task()
        assertSuccess {
            repository.save(operationId = OPERATION_ID, task = task)
        }

        val tasks = assertSuccess {
            repository.load(operationId = OPERATION_ID)
        }

        assertEquals(1, tasks.size)
        assertEquals(task, tasks[0])

        val dupSave = assertSuccess {
            repository.save(operationId = OPERATION_ID, task = task)
        }

        assertFalse(dupSave)
    }

    @Test
    fun errorSavingTask() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val fail: Fail.Incident.Database.Access = assertFailure {
            repository.save(operationId = OPERATION_ID, task = task())
        }

        assertEquals("Error accessing to database.", fail.description)
    }

    @Test
    fun errorLoadingTask() {
        val unknownAction = "Unknown"
        insertTask(operationId = OPERATION_ID, action = unknownAction)

        val fail: Fail.Incident.Database.Data = assertFailure {
            repository.load(operationId = OPERATION_ID)
        }

        assertEquals("Error of converting loaded name of task action '$unknownAction' to enum item.", fail.description)
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
                  operation_id TEXT,
                  timestamp    TIMESTAMP,
                  cpid         TEXT,
                  ocid         TEXT,
                  action_name  TEXT,
                  data         TEXT,
                  PRIMARY KEY (operation_id, timestamp, cpid, ocid, action_name)
                );
            """
        )
    }

    private fun insertTask(
        operationId: OperationId = OPERATION_ID,
        cpid: Cpid = CPID,
        ocid: Ocid = OCID,
        timestamp: LocalDateTime = TIMESTAMP,
        action: String = ACTION.key,
        taskData: String = TASK_DATA
    ) {
        val insert = QueryBuilder.insertInto(KEY_SPACE, TABLE_NAME)
            .value(COLUMN_OPERATION_ID, operationId.toString())
            .value(COLUMN_TIMESTAMP, timestamp.toCassandraTimestamp())
            .value(COLUMN_CPID, cpid.toString())
            .value(COLUMN_OCID, ocid.toString())
            .value(COLUMN_ACTION_NAME, action)
            .value(COLUMN_TASK_DATA, taskData)

        session.execute(insert)
    }

    private fun task(
        cpid: Cpid = CPID,
        ocid: Ocid = OCID,
        timestamp: LocalDateTime = TIMESTAMP,
        action: QueueNoticeTask.Action = ACTION,
        taskData: String = TASK_DATA
    ) = QueueNoticeTask(
        id = QueueNoticeTask.Id(
            cpid = cpid,
            ocid = ocid,
            action = action
        ),
        timestamp = timestamp,
        data = taskData
    )
}
