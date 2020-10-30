package com.procurement.orchestrator.infrastructure.repository

import com.datastax.driver.core.Session
import com.procurement.orchestrator.application.repository.OldProcessContextRepository
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
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
        save(storingKey = cpid.toString(), context = context)

    override fun save(ocid: Ocid, context: String): Result<Boolean, Fail.Incident.Database.Access> =
        save(storingKey = ocid.toString(), context = context)

    private fun save(storingKey: String, context: String): Result<Boolean, Fail.Incident.Database.Access> =
        preparedSaveCQL.bind()
            .apply {
                setString(columnCpid, storingKey)
                setString(columnContext, context)
            }
            .tryExecute(session)
            .map { it.wasApplied() }

    override fun load(cpid: Cpid): Result<String?, Fail.Incident.Database> = load(cpid.toString())

    override fun load(ocid: Ocid): Result<String?, Fail.Incident.Database> = load(ocid.toString())

    private fun load(key: String): Result<String?, Fail.Incident.Database> = preparedLoadCQL.bind()
        .apply {
            setString(columnCpid, key)
        }
        .tryExecute(session)
        .orForwardFail { fail -> return fail }
        .one()
        ?.getString(columnContext)
        .asSuccess()
}
