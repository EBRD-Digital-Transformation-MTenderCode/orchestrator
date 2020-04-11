package com.procurement.orchestrator.infrastructure.repository

import com.datastax.driver.core.Session
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.infrastructure.bpms.repository.ErrorDescriptionRepository
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetErrorDescriptionsAction
import com.procurement.orchestrator.infrastructure.extension.cassandra.tryExecute

class CassandraErrorDescriptionRepository(private val session: Session) : ErrorDescriptionRepository {

    companion object {
        private const val keySpace = "orchestrator"
        private const val tableName = "error_descriptions"
        private const val columnCode = "code"
        private const val columnLanguage = "language"
        private const val columnDescription = "description"

        private const val LOAD_CQL = """
               SELECT $columnCode,
                      $columnDescription
                 FROM $keySpace.$tableName
                WHERE $columnCode IN :codes
                  AND $columnLanguage=?;
            """
    }

    private val preparedLoadCQL = session.prepare(LOAD_CQL)

    override fun load(
        codes: List<String>,
        language: String
    ): Result<GetErrorDescriptionsAction.Result, Fail.Incident.Database> =
        preparedLoadCQL.bind()
            .apply {
                setList("codes", codes.toList())
                setString(columnLanguage, language)
            }
            .tryExecute(session)
            .orForwardFail { fail -> return fail }
            .map { row ->
                GetErrorDescriptionsAction.Result.Error(
                    code = row.getString(columnCode),
                    description = row.getString(columnDescription)
                )
            }
            .let {
                GetErrorDescriptionsAction.Result(values = it)
            }
            .asSuccess<GetErrorDescriptionsAction.Result, Fail.Incident.Database>()
}
