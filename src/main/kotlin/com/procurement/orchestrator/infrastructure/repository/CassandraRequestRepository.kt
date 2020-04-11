package com.procurement.orchestrator.infrastructure.repository

import com.datastax.driver.core.Session
import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.PlatformId
import com.procurement.orchestrator.application.model.RequestId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.infrastructure.bpms.repository.RequestRecord
import com.procurement.orchestrator.infrastructure.bpms.repository.RequestRepository
import com.procurement.orchestrator.infrastructure.extension.cassandra.toCassandraTimestamp
import com.procurement.orchestrator.infrastructure.extension.cassandra.tryExecute
import java.time.LocalDateTime

class CassandraRequestRepository(private val session: Session) : RequestRepository {

    companion object {
        private const val keySpace = "orchestrator"
        private const val tableName = "requests"
        private const val columnOperationId = "operation_id"
        private const val columnTimestamp = "timestamp"
        private const val columnRequestId = "request_id"
        private const val columnPlatformId = "platform_id"
        private const val columnContext = "context"
        private const val columnPayload = "payload"

        private const val SAVE_REQUEST_CQL = """
               INSERT INTO $keySpace.$tableName(
                      $columnOperationId,
                      $columnTimestamp,
                      $columnRequestId,
                      $columnPlatformId,
                      $columnContext,
                      $columnPayload
               )
               VALUES(?, ?, ?, ?, ?,?)
               IF NOT EXISTS;
            """

        private const val LOAD_REQUEST_CQL = """
               SELECT $columnPlatformId,
                      $columnContext,
                      $columnPayload
                 FROM $keySpace.$tableName
                WHERE $columnOperationId=?
                  AND $columnTimestamp=?
                  AND $columnRequestId=?;
            """
    }

    private val preparedSaveRequestCQL = session.prepare(SAVE_REQUEST_CQL)
    private val preparedLoadRequestCQL = session.prepare(LOAD_REQUEST_CQL)

    override fun save(requestRecord: RequestRecord): Result<Boolean, Fail.Incident.Database.Access> =
        preparedSaveRequestCQL.bind()
            .apply {
                setString(columnOperationId, requestRecord.operationId.toString())
                setTimestamp(columnTimestamp, requestRecord.timestamp.toCassandraTimestamp())
                setString(columnRequestId, requestRecord.requestId.toString())
                setString(columnPlatformId, requestRecord.platformId.toString())
                setString(columnContext, requestRecord.context)
                setString(columnPayload, requestRecord.payload)
            }
            .tryExecute(session)
            .map { it.wasApplied() }

    override fun load(
        operationId: OperationId,
        timestamp: LocalDateTime,
        requestId: RequestId
    ): Result<RequestRecord?, Fail.Incident.Database> =
        preparedLoadRequestCQL.bind()
            .apply {
                setString(columnOperationId, operationId.toString())
                setTimestamp(columnTimestamp, timestamp.toCassandraTimestamp())
                setString(columnRequestId, requestId.toString())
            }
            .tryExecute(session)
            .orForwardFail { fail -> return fail }
            .one()
            ?.let { row ->
                RequestRecord(
                    operationId = operationId,
                    timestamp = timestamp,
                    requestId = requestId,
                    platformId = row.getString(columnPlatformId)
                        .let { value ->
                            PlatformId.tryCreateOrNull(value)
                                ?: return failure(Fail.Incident.Database.Data(description = "Error of converting loaded platformId '$value' to object PlatformId."))
                        },
                    context = row.getString(columnContext),
                    payload = row.getString(columnPayload)
                )
            }
            .asSuccess()
}
