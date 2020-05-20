package com.procurement.orchestrator.infrastructure.repository

import com.datastax.driver.core.Session
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStep
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.extension.cassandra.toCassandraTimestamp
import com.procurement.orchestrator.infrastructure.extension.cassandra.tryExecute

class CassandraOperationStepRepository(private val session: Session) : OperationStepRepository {

    companion object {
        private const val keySpace = "orchestrator"
        private const val tableName = "steps"
        private const val columnCpid = "cpid"
        private const val columnOperationId = "operation_id"
        private const val columnProcessId = "process_id"
        private const val columnTaskId = "task_id"
        private const val columnStepDate = "step_date"
        private const val columnRequest = "request"
        private const val columnResponse = "response"
        private const val columnContext = "context"

        private const val SAVE_CQL = """
               INSERT INTO $keySpace.$tableName(
                      $columnCpid,
                      $columnOperationId,
                      $columnProcessId,
                      $columnTaskId,
                      $columnStepDate,
                      $columnRequest,
                      $columnResponse,
                      $columnContext
               )
               VALUES(?, ?, ?, ?, ?, ?, ?, ?)
               IF NOT EXISTS;
            """
    }

    private val preparedSaveCQL = session.prepare(SAVE_CQL)

    override fun save(step: OperationStep): Result<Boolean, Fail.Incident.Database.Access> = preparedSaveCQL.bind()
        .apply {
            setString(columnCpid, step.cpid.toString())
            setString(columnOperationId, step.operationId.toString())
            setString(columnProcessId, step.processId)
            setString(columnTaskId, step.taskId)
            setTimestamp(columnStepDate, step.stepDate.toCassandraTimestamp())
            setString(columnRequest, step.request)
            setString(columnResponse, step.response)
            setString(columnContext, step.context)
        }
        .tryExecute(session)
        .map { it.wasApplied() }
}
