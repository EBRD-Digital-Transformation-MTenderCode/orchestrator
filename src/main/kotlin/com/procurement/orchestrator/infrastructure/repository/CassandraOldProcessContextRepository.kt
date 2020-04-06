package com.procurement.orchestrator.infrastructure.repository

import com.datastax.driver.core.Session
import com.procurement.orchestrator.application.repository.OldProcessContextRepository
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.infrastructure.extension.cassandra.tryExecute

class CassandraOldProcessContextRepository(private val session: Session) : OldProcessContextRepository {

    companion object {
        private const val keySpace = "ocds"
        private const val tableName = "orchestrator_context"
        private const val columnCpid = "cp_id"
        private const val columnContext = "context"

        private const val SAVE_CQL = """
               INSERT INTO ${keySpace}.${tableName}(
                      $columnCpid,
                      $columnContext
               )
               VALUES(?, ?);
            """

        private const val LOAD_CQL = """
               SELECT $columnContext
                 FROM $keySpace.$tableName
                WHERE $columnCpid=?;
            """
    }

    private val preparedSaveCQL = session.prepare(SAVE_CQL)
    private val preparedLoadCQL = session.prepare(LOAD_CQL)

    override fun save(cpid: Cpid, context: String): Result<Boolean, Fail.Incident.Database.Access> =
        preparedSaveCQL.bind()
            .apply {
                setString(columnCpid, cpid.toString())
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
