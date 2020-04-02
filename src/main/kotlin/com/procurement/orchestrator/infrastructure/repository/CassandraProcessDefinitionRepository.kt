package com.procurement.orchestrator.infrastructure.repository

import com.datastax.driver.core.Session
import com.procurement.orchestrator.application.model.process.ProcessDefinitionKey
import com.procurement.orchestrator.application.repository.ProcessDefinitionRepository
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.ProcurementMethod
import com.procurement.orchestrator.domain.model.address.country.CountryId
import com.procurement.orchestrator.infrastructure.extension.cassandra.tryExecute

class CassandraProcessDefinitionRepository(private val session: Session) : ProcessDefinitionRepository {

    companion object {
        private const val keySpace = "orchestrator"
        private const val tableName = "process_definitions"
        private const val columnCountry = "country"
        private const val columnPmd = "pmd"
        private const val columnProcessName = "process_name"
        private const val columnProcessDefinitionKey = "process_definition_key"

        private const val GET_PROCESS_DEFINITION_KEY_CQL = """
               SELECT $columnProcessDefinitionKey
                 FROM $keySpace.$tableName
                WHERE $columnCountry=?
                  AND $columnPmd=?
                  AND $columnProcessName=?;
            """
    }

    private val preparedGetProcessDefinitionKeyCQL = session.prepare(GET_PROCESS_DEFINITION_KEY_CQL)

    override fun getProcessDefinitionKey(
        countryId: CountryId,
        pmd: ProcurementMethod,
        processName: String
    ): Result<ProcessDefinitionKey?, Fail.Incident.Database.Access> = preparedGetProcessDefinitionKeyCQL.bind()
        .apply {
            setString(columnCountry, countryId)
            setString(columnPmd, pmd.name)
            setString(columnProcessName, processName)
        }
        .tryExecute(session)
        .orReturnFail { return failure(it) }
        .one()
        ?.let { row -> ProcessDefinitionKey(row.getString(columnProcessDefinitionKey)) }
        .asSuccess()
}
