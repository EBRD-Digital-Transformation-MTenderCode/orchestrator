package com.procurement.orchestrator.infrastructure.repository

import com.datastax.driver.core.Session
import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.infrastructure.bpms.repository.NoticeQueueRepository
import com.procurement.orchestrator.infrastructure.bpms.repository.NoticeTask
import com.procurement.orchestrator.infrastructure.extension.cassandra.toCassandraTimestamp
import com.procurement.orchestrator.infrastructure.extension.cassandra.toLocalDateTime
import com.procurement.orchestrator.infrastructure.extension.cassandra.tryExecute

class CassandraNoticeQueueRepository(private val session: Session) : NoticeQueueRepository {

    companion object {
        private const val keySpace = "orchestrator"
        private const val tableName = "notice_queue"
        private const val columnOperationId = "operation_id"
        private const val columnTimestamp = "timestamp"
        private const val columnCpid = "cpid"
        private const val columnOcid = "ocid"
        private const val columnAction = "action_name"
        private const val columnData = "data"

        private const val SAVE_CQL = """
               INSERT INTO $keySpace.$tableName(
                      $columnOperationId,
                      $columnTimestamp,
                      $columnCpid,
                      $columnOcid,
                      $columnAction,
                      $columnData
               )
               VALUES(?, ?, ?, ?, ?, ?)
               IF NOT EXISTS;
            """

        private const val LOAD_CQL = """
               SELECT $columnTimestamp,
                      $columnCpid,
                      $columnOcid,
                      $columnAction,
                      $columnData
                 FROM $keySpace.$tableName
                WHERE $columnOperationId=?;
            """
    }

    private val preparedSaveCQL = session.prepare(SAVE_CQL)
    private val preparedLoadCQL = session.prepare(LOAD_CQL)

    override fun save(task: NoticeTask): Result<Boolean, Fail.Incident.Database.Access> = preparedSaveCQL.bind()
        .apply {
            setString(columnOperationId, task.operationId.toString())
            setTimestamp(columnTimestamp, task.timestamp.toCassandraTimestamp())
            setString(columnCpid, task.cpid.toString())
            setString(columnOcid, task.ocid.toString())
            setString(columnAction, task.action.key)
            setString(columnData, task.data)
        }
        .tryExecute(session)
        .map { it.wasApplied() }

    override fun load(operationId: OperationId): Result<List<NoticeTask>, Fail.Incident.Database> =
        preparedLoadCQL.bind()
            .apply {
                setString(columnOperationId, operationId.toString())
            }
            .tryExecute(session)
            .doOnError { return failure(it) }
            .get
            .map { row ->
                NoticeTask(
                    operationId = operationId,
                    timestamp = row.getTimestamp(columnTimestamp).toLocalDateTime(),
                    cpid = row.getString(columnCpid)
                        .let { value ->
                            Cpid.tryCreateOrNull(value)
                                ?: return failure(Fail.Incident.Database.Data(description = "Error of converting loaded cpid '$value' to object Cpid."))
                        },
                    ocid = row.getString(columnOcid)
                        .let { value ->
                            Ocid.tryCreateOrNull(value)
                                ?: return failure(Fail.Incident.Database.Data(description = "Error of converting loaded ocid '$value' to object Ocid."))
                        },
                    action = row.getString(columnAction)
                        .let { value ->
                            NoticeTask.Action.orNull(value)
                                ?: return failure(Fail.Incident.Database.Data(description = "Error of converting loaded name of task action '$value' to enum item."))
                        },
                    data = row.getString(columnData)
                )
            }
            .asSuccess()
}
