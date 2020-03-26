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
import com.procurement.orchestrator.application.repository.OldProcessContextRepository
import com.procurement.orchestrator.domain.extension.date.nowDefaultUTC
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.extension.junit.assertFailure
import com.procurement.orchestrator.extension.junit.assertSuccess
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

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DatabaseTestConfiguration::class])
class CassandraOldProcessContextRepositoryIT {

    companion object {
        private const val KEY_SPACE = "ocds"
        private const val TABLE_NAME = "orchestrator_context"

        private val CPID: Cpid = Cpid.generate(prefix = "ocds-t1t2t3", country = "MD", timestamp = nowDefaultUTC())
        private const val CONTEXT_1: String = "context-1"
        private const val CONTEXT_2: String = "context-2"
    }

    @Autowired
    private lateinit var container: CassandraTestContainer

    private lateinit var session: Session
    private lateinit var repository: OldProcessContextRepository

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

        repository = CassandraOldProcessContextRepository(session)
    }

    @AfterEach
    fun clean() {
        dropKeyspace()
    }

    @Test
    fun savingOnceContext() {

        val wasSaved = assertSuccess {
            repository.save(cpid = CPID, context = CONTEXT_1)
        }
        assertTrue(wasSaved)

        val context = assertSuccess {
            repository.load(cpid = CPID)
        }

        assertEquals(CONTEXT_1, context)
    }

    @Test
    fun savingTwiceContext() {

        val wasSavedFirst = assertSuccess {
            repository.save(cpid = CPID, context = CONTEXT_1)
        }
        assertTrue(wasSavedFirst)

        val wasSavedSecond = assertSuccess {
            repository.save(cpid = CPID, context = CONTEXT_2)
        }
        assertTrue(wasSavedSecond)

        val lastSavedContext = assertSuccess {
            repository.load(cpid = CPID)
        }

        assertEquals(CONTEXT_2, lastSavedContext)
    }

    @Test
    fun errorSavingContext() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val fail: Fail.Incident.Database.Access = assertFailure {
            repository.save(cpid = CPID, context = CONTEXT_1)
        }

        assertEquals("Error accessing to database.", fail.description)
    }

    @Test
    fun loadingContext() {
        val wasSaved = assertSuccess {
            repository.save(cpid = CPID, context = CONTEXT_1)
        }
        assertTrue(wasSaved)

        val context = assertSuccess {
            repository.load(cpid = CPID)
        }

        assertNotNull(context)
        assertEquals(CONTEXT_1, context)
    }

    @Test
    fun errorLoadingContext() {
        val context = assertSuccess {
            repository.load(cpid = CPID)
        }

        assertNull(context)
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
                  cp_id   TEXT,
                  context TEXT,
                  PRIMARY KEY (cp_id)
                );
            """
        )
    }
}
