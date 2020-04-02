package com.procurement.orchestrator.infrastructure.repository

import com.datastax.driver.core.Session
import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.repository.ProcessContextRepository
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.infrastructure.extension.cassandra.toCassandraTimestamp
import com.procurement.orchestrator.infrastructure.extension.cassandra.tryExecute
import java.time.LocalDateTime

class CassandraProcessContextRepository(private val session: Session) : ProcessContextRepository {

    companion object {
        private const val keySpace = "orchestrator"
        private const val tableName = "process_context"
        private const val columnCpid = "cpid"
        private const val columnTimestamp = "timestamp"
        private const val columnOperationId = "operation_id"
        private const val columnContext = "context"

        private const val SAVE_CQL = """
               INSERT INTO $keySpace.$tableName(
                      $columnCpid,
                      $columnTimestamp,
                      $columnOperationId,
                      $columnContext
               )
               VALUES(?, ?, ?, ?)
               IF NOT EXISTS;
            """

        private const val LOAD_CQL = """
               SELECT $columnContext
                 FROM $keySpace.$tableName
                WHERE $columnCpid=?
                LIMIT 1;
            """
    }

    private val preparedSaveCQL = session.prepare(SAVE_CQL)
    private val preparedLoadCQL = session.prepare(LOAD_CQL)

    override fun save(
        cpid: Cpid,
        timestamp: LocalDateTime,
        operationId: OperationId,
        context: String
    ): Result<Boolean, Fail.Incident.Database.Access> = preparedSaveCQL.bind()
        .apply {
            setString(columnCpid, cpid.toString())
            setTimestamp(columnTimestamp, timestamp.toCassandraTimestamp())
            setString(columnOperationId, operationId.toString())
            setString(columnContext, context)
        }
        .tryExecute(session)
        .map { it.wasApplied() }

    override fun load(cpid: Cpid): Result<String?, Fail.Incident.Database> = preparedLoadCQL.bind()
        .apply {
            setString(columnCpid, cpid.toString())
        }
        .tryExecute(session)
        .orReturnFail { return failure(it) }
        .one()
        ?.getString(columnContext)
        .asSuccess()
}
