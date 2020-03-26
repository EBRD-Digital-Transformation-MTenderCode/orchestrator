package com.procurement.orchestrator.infrastructure.repository

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.Cluster
import com.datastax.driver.core.HostDistance
import com.datastax.driver.core.PlainTextAuthProvider
import com.datastax.driver.core.PoolingOptions
import com.datastax.driver.core.Session
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.whenever
import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.repository.LaunchedProcessInfo
import com.procurement.orchestrator.application.repository.ProcessInitializerRepository
import com.procurement.orchestrator.domain.extension.date.nowDefaultUTC
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.extension.junit.assertFailure
import com.procurement.orchestrator.extension.junit.assertSuccess
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DatabaseTestConfiguration::class])
class CassandraProcessInitializerRepositoryIT {
    companion object {
        private const val KEY_SPACE = "orchestrator"
        private const val TABLE_NAME = "launched_processes"

        private val OPERATION_ID: OperationId = OperationId.tryCreateOrNull("8025d29c-f0e0-4a31-92c3-1a4a65065fc0")!!
        private val TIMESTAMP: LocalDateTime = LocalDateTime.now()
        private const val PROCESS_ID: String = "process-id-1"
        private val CPID: Cpid = Cpid.generate(prefix = "ocds-t1t2t3", country = "MD", timestamp = nowDefaultUTC())
    }

    @Autowired
    private lateinit var container: CassandraTestContainer

    private lateinit var session: Session
    private lateinit var repository: ProcessInitializerRepository

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

        repository = CassandraProcessInitializerRepository(session)
    }

    @AfterEach
    fun clean() {
        dropKeyspace()
    }

    @Test
    fun launchProcess() {
        val launchedProcessInfo = assertSuccess {
            repository.launchProcess(
                operationId = OPERATION_ID,
                timestamp = TIMESTAMP,
                processId = PROCESS_ID,
                cpid = CPID
            )
        }

        assertTrue(launchedProcessInfo.wasLaunched)
    }

    @Test
    fun launchDuplicateProcess() {
        val firstLaunchedProcessInfo: LaunchedProcessInfo = assertSuccess {
            repository.launchProcess(
                operationId = OPERATION_ID,
                timestamp = TIMESTAMP,
                processId = PROCESS_ID,
                cpid = CPID
            )
        }

        assertTrue(firstLaunchedProcessInfo.wasLaunched)

        val secondLaunchedProcessInfo = assertSuccess {
            repository.launchProcess(
                operationId = OPERATION_ID,
                timestamp = TIMESTAMP,
                processId = PROCESS_ID,
                cpid = CPID
            )
        }

        assertFalse(secondLaunchedProcessInfo.wasLaunched)
    }

    @Test
    fun errorLaunchProcess() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val fail: Fail.Incident.Database.Access = assertFailure {
            repository.launchProcess(
                operationId = OPERATION_ID,
                timestamp = TIMESTAMP,
                processId = PROCESS_ID,
                cpid = CPID
            )
        }
        assertEquals("Error accessing to database.", fail.description)
    }

    @Test
    fun isLaunchedProcess() {
        val launchedProcessInfo = assertSuccess {
            repository.launchProcess(
                operationId = OPERATION_ID,
                timestamp = TIMESTAMP,
                processId = PROCESS_ID,
                cpid = CPID
            )
        }
        assertTrue(launchedProcessInfo.wasLaunched)

        val isStarted = assertSuccess {
            repository.isLaunchedProcess(operationId = OPERATION_ID)
        }
        assertTrue(isStarted)
    }

    @Test
    fun isNotLaunchedProcess() {

        val isStarted = assertSuccess {
            repository.isLaunchedProcess(operationId = OPERATION_ID)
        }
        assertFalse(isStarted)
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
                  process_id   TEXT,
                  cpid         TEXT,
                  PRIMARY KEY (operation_id)
                );
            """
        )
    }
}
