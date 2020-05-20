package com.procurement.orchestrator.infrastructure.repository

import com.datastax.driver.core.Session
import com.procurement.orchestrator.application.model.Phase
import com.procurement.orchestrator.application.model.Rule
import com.procurement.orchestrator.application.model.Stage
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.model.process.ProcessDefinitionKey
import com.procurement.orchestrator.application.repository.RuleRepository
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.ProcurementMethod
import com.procurement.orchestrator.domain.model.address.country.CountryId
import com.procurement.orchestrator.infrastructure.extension.cassandra.tryExecute

class CassandraRuleRepository(private val session: Session) : RuleRepository {

    companion object {
        private const val keySpace = "orchestrator"
        private const val tableName = "rules"
        private const val columnCountry = "country"
        private const val columnPmd = "pmd"
        private const val columnProcessDefinitionKey = "process_definition_key"
        private const val columnStageFrom = "stage_from"
        private const val columnStageTo = "stage_to"
        private const val columnPhaseFrom = "phase_from"
        private const val columnPhaseTo = "phase_to"
        private const val columnOperationType = "operation_type"

        private const val LOAD_RULES_CQL = """
               SELECT $columnStageFrom,
                      $columnStageTo,
                      $columnPhaseFrom,
                      $columnPhaseTo,
                      $columnOperationType
                 FROM $keySpace.$tableName
                WHERE $columnCountry=?
                  AND $columnPmd=?
                  AND $columnProcessDefinitionKey=?;
            """

        private const val LOAD_RULE_CQL = """
               SELECT $columnStageTo,
                      $columnPhaseTo,
                      $columnOperationType
                 FROM $keySpace.$tableName
                WHERE $columnCountry=?
                  AND $columnPmd=?
                  AND $columnProcessDefinitionKey=?
                  AND $columnStageFrom=?
                  AND $columnPhaseFrom=?;
            """
    }

    private val preparedLoadRulesCQL = session.prepare(LOAD_RULES_CQL)
    private val preparedLoadRuleCQL = session.prepare(LOAD_RULE_CQL)

    override fun load(
        countryId: CountryId,
        pmd: ProcurementMethod,
        processDefinitionKey: ProcessDefinitionKey
    ): Result<List<Rule>, Fail.Incident.Database> = preparedLoadRulesCQL.bind()
        .apply {
            setString(columnCountry, countryId)
            setString(columnPmd, pmd.name)
            setString(columnProcessDefinitionKey, processDefinitionKey.toString())
        }
        .tryExecute(session)
        .orForwardFail { fail -> return fail }
        .map { row ->
            Rule(
                countryId = countryId,
                pmd = pmd,
                processDefinitionKey = processDefinitionKey,
                stageFrom = row.getString(columnStageFrom)
                    ?.let { value ->
                        Stage.orNull(value)
                            ?: return failure(Fail.Incident.Database.Data(description = "Error of converting loaded prev stage '$value' to enum Stage."))
                    },
                stageTo = row.getString(columnStageTo)
                    .let { value ->
                        Stage.orNull(value)
                            ?: return failure(Fail.Incident.Database.Data(description = "Error of converting loaded new stage '$value' to enum Stage."))
                    },
                phaseFrom = row.getString(columnPhaseFrom)
                    ?.let { Phase(it) },
                phaseTo = Phase(row.getString(columnPhaseTo)),
                operationType = row.getString(columnOperationType)
                    .let { value ->
                        OperationTypeProcess.orNull(value)
                            ?: return failure(Fail.Incident.Database.Data(description = "Error of converting loaded operation type '$value' to enum OperationType."))
                    }
            )
        }
        .asSuccess()

    override fun load(
        countryId: CountryId,
        pmd: ProcurementMethod,
        processDefinitionKey: ProcessDefinitionKey,
        stageFrom: Stage?,
        phaseFrom: Phase?
    ): Result<Rule?, Fail.Incident.Database> = preparedLoadRuleCQL.bind()
        .apply {
            setString(columnCountry, countryId)
            setString(columnPmd, pmd.name)
            setString(columnProcessDefinitionKey, processDefinitionKey.toString())
            setString(columnStageFrom, stageFrom?.toString() ?: "")
            setString(columnPhaseFrom, phaseFrom?.toString() ?: "")
        }
        .tryExecute(session)
        .orForwardFail { fail -> return fail }
        .one()
        ?.let { row ->
            Rule(
                countryId = countryId,
                pmd = pmd,
                processDefinitionKey = processDefinitionKey,
                stageFrom = stageFrom,
                stageTo = row.getString(columnStageTo)
                    .let { value ->
                        Stage.orNull(value)
                            ?: return failure(Fail.Incident.Database.Data(description = "Error of converting loaded new stage '$value' to enum Stage."))
                    },
                phaseFrom = phaseFrom,
                phaseTo = Phase(row.getString(columnPhaseTo)),
                operationType = row.getString(columnOperationType)
                    .let { value ->
                        OperationTypeProcess.orNull(value)
                            ?: return failure(Fail.Incident.Database.Data(description = "Error of converting loaded operation type '$value' to enum OperationType."))
                    }
            )
        }
        .asSuccess()
}
