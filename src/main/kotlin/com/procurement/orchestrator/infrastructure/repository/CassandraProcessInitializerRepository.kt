package com.procurement.orchestrator.infrastructure.repository

import com.datastax.driver.core.Session
import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.process.ProcessId
import com.procurement.orchestrator.application.repository.LaunchedProcessInfo
import com.procurement.orchestrator.application.repository.ProcessInitializerRepository
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.infrastructure.extension.cassandra.toCassandraTimestamp
import com.procurement.orchestrator.infrastructure.extension.cassandra.toLocalDateTime
import com.procurement.orchestrator.infrastructure.extension.cassandra.tryExecute
import java.time.LocalDateTime

class CassandraProcessInitializerRepository(private val session: Session) : ProcessInitializerRepository {

    companion object {
        private const val keySpace = "orchestrator"
        private const val tableName = "launched_processes"
        private const val columnOperationId = "operation_id"
        private const val columnTimestamp = "timestamp"
        private const val columnProcessId = "process_id"
        private const val columnCpid = "cpid"

        private const val SAVE_LAUNCHED_PROCESS_INFO_CQL = """
               INSERT INTO $keySpace.$tableName(
                      $columnOperationId,
                      $columnTimestamp,
                      $columnProcessId,
                      $columnCpid
               )
               VALUES(?, ?, ?, ?)
               IF NOT EXISTS;
            """

        private const val CHECKING_LAUNCHED_PROCESS_CQL = """
               SELECT $columnProcessId,
                      $columnCpid
                 FROM $keySpace.$tableName
                WHERE $columnOperationId=?;
            """
    }

    private val preparedSaveLaunchedProcessCQL = session.prepare(SAVE_LAUNCHED_PROCESS_INFO_CQL)
    private val preparedCheckLaunchedProcessCQL = session.prepare(CHECKING_LAUNCHED_PROCESS_CQL)

    override fun launchProcess(
        operationId: OperationId,
        timestamp: LocalDateTime,
        processId: ProcessId,
        cpid: Cpid
    ): Result<LaunchedProcessInfo, Fail.Incident.Database> {
        val resultSet = preparedSaveLaunchedProcessCQL.bind()
            .apply {
                setString(columnOperationId, operationId.toString())
                setTimestamp(columnTimestamp, timestamp.toCassandraTimestamp())
                setString(columnProcessId, processId)
                setString(columnCpid, cpid.toString())
            }
            .tryExecute(session)
            .orReturnFail { return failure(it) }

        val launchedProcessInfo = if (resultSet.wasApplied()) {
            LaunchedProcessInfo(
                wasLaunched = true,
                operationId = operationId,
                timestamp = timestamp,
                processId = processId,
                cpid = cpid
            )
        } else {
            val row = resultSet.one()
            LaunchedProcessInfo(
                wasLaunched = false,
                operationId = operationId,
                timestamp = row.getTimestamp(columnTimestamp).toLocalDateTime(),
                processId = row.getString(columnProcessId),
                cpid = row.getString(columnCpid)
                    .let { value ->
                        Cpid.tryCreateOrNull(value)
                            ?: return failure(Fail.Incident.Database.Data(description = "Error of converting loaded cpid '$value' to object Cpid."))
                    }
            )
        }

        return success(launchedProcessInfo)
    }

    override fun isLaunchedProcess(operationId: OperationId): Result<Boolean, Fail.Incident.Database.Access> {
        val row = preparedCheckLaunchedProcessCQL.bind()
            .apply {
                setString(columnOperationId, operationId.toString())
            }
            .tryExecute(session)
            .orReturnFail { return failure(it) }
            .one()

        return success(row != null)
    }
}
