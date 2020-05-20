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
import com.procurement.orchestrator.application.model.PlatformId
import com.procurement.orchestrator.application.model.RequestId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.extension.junit.assertFailure
import com.procurement.orchestrator.extension.junit.assertSuccess
import com.procurement.orchestrator.infrastructure.bpms.repository.RequestRecord
import com.procurement.orchestrator.infrastructure.bpms.repository.RequestRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
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
class CassandraRequestRepositoryIT {
    companion object {
        private const val KEY_SPACE = "orchestrator"
        private const val TABLE_NAME = "requests"

        private val OPERATION_ID: OperationId = OperationId.tryCreateOrNull("8025d29c-f0e0-4a31-92c3-1a4a65065fc0")!!
        private val TIMESTAMP: LocalDateTime = LocalDateTime.now()
        private val REQUEST_ID: RequestId = RequestId.tryCreateOrNull("bb3b7e46-e327-4a1c-87a8-24bc0b016ac3")!!
        private val PLATFORM_ID: PlatformId = PlatformId.tryCreateOrNull("45613080-b8b8-4f93-b382-65124f9acef2")!!
        private const val CONTEXT: String = "context"
        private const val PAYLOAD: String = "payload"
    }

    @Autowired
    private lateinit var container: CassandraTestContainer

    private lateinit var session: Session
    private lateinit var repository: RequestRepository

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

        repository = CassandraRequestRepository(session)
    }

    @AfterEach
    fun clean() {
        dropKeyspace()
    }

    @Test
    fun savingAndLoadingRequest() {
        val record = requestRecord(operationId = OPERATION_ID, timestamp = TIMESTAMP, requestId = REQUEST_ID)
        val isSaved = assertSuccess {
            repository.save(record)
        }

        assertTrue(isSaved)

        val actualRecord = assertSuccess {
            repository.load(operationId = OPERATION_ID, timestamp = TIMESTAMP, requestId = REQUEST_ID)
        }

        assertNotNull(actualRecord)
        assertEquals(record, actualRecord)
    }

    @Test
    fun errorSavingRequest() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val fail: Fail.Incident.Database.Access = assertFailure {
            val record = requestRecord(operationId = OPERATION_ID, timestamp = TIMESTAMP, requestId = REQUEST_ID)
            repository.save(record)
        }

        assertEquals("Error accessing to database.", fail.description)
    }

    @Test
    fun errorLoadRequest() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val fail: Fail.Incident.Database.Access = assertFailure {
            repository.load(operationId = OPERATION_ID, timestamp = TIMESTAMP, requestId = REQUEST_ID)
        }

        assertEquals("Error accessing to database.", fail.description)
    }

    @Test
    fun requestNotFound() {
        val unknownRequestId: RequestId = RequestId.tryCreateOrNull("a214f779-4986-4a49-8020-56a2bb7620be")!!
        val record = requestRecord(operationId = OPERATION_ID, timestamp = TIMESTAMP, requestId = unknownRequestId)
        val isSaved = assertSuccess {
            repository.save(record)
        }

        assertTrue(isSaved)

        val result = assertSuccess {
            repository.load(operationId = OPERATION_ID, timestamp = TIMESTAMP, requestId = REQUEST_ID)
        }

        assertNull(result)
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
                  request_id   TEXT,
                  platform_id  TEXT,
                  context      TEXT,
                  payload      TEXT,
                  PRIMARY KEY (operation_id, timestamp, request_id)
                );
            """
        )
    }

    private fun requestRecord(
        requestId: RequestId = REQUEST_ID,
        timestamp: LocalDateTime = TIMESTAMP,
        operationId: OperationId = OPERATION_ID,
        platformId: PlatformId = PLATFORM_ID,
        context: String = CONTEXT,
        payload: String = PAYLOAD
    ) = RequestRecord(
        requestId = requestId,
        timestamp = timestamp,
        operationId = operationId,
        platformId = platformId,
        context = context,
        payload = payload
    )
}
