package com.procurement.orchestrator.application.service

import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.Phase
import com.procurement.orchestrator.application.model.Stage
import com.procurement.orchestrator.application.model.process.LatestProcessContext
import com.procurement.orchestrator.application.model.process.OldProcessContext
import com.procurement.orchestrator.application.model.process.ProcessDefinitionKey
import com.procurement.orchestrator.application.repository.OldProcessContextRepository
import com.procurement.orchestrator.application.repository.ProcessDefinitionRepository
import com.procurement.orchestrator.application.repository.ProcessInitializerRepository
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.fail.error.BpeErrors
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.ProcurementMethodDetails
import com.procurement.orchestrator.domain.model.address.country.CountryId
import com.procurement.orchestrator.domain.model.tender.AwardCriteria
import org.camunda.bpm.engine.RuntimeService

interface ProcessService {
    fun getProcessDefinitionKey(
        countryId: CountryId,
        pmd: ProcurementMethodDetails,
        processName: String
    ): Result<ProcessDefinitionKey, Fail>

    fun getProcessContext(cpid: Cpid, ocid: Ocid): Result<LatestProcessContext?, Fail.Incident>

    fun launchProcess(
        processDefinitionKey: ProcessDefinitionKey,
        operationId: OperationId,
        variables: Map<String, Any>
    )

    fun isLaunchedProcess(operationId: OperationId): Result<Boolean, Fail.Incident>
}

class ProcessServiceImpl(
    private val transform: Transform,
    private val processDefinitionRepository: ProcessDefinitionRepository,
    private val oldProcessContextRepository: OldProcessContextRepository,
    private val processInitializerRepository: ProcessInitializerRepository,
    private val runtimeService: RuntimeService
) : ProcessService {

    override fun getProcessDefinitionKey(
        countryId: CountryId,
        pmd: ProcurementMethodDetails,
        processName: String
    ): Result<ProcessDefinitionKey, Fail> =
        processDefinitionRepository.getProcessDefinitionKey(countryId = countryId, pmd = pmd, processName = processName)
            .orForwardFail { fail -> return fail }
            ?.asSuccess()
            ?: failure(
                BpeErrors.Process(
                    description = "It is impossible to start the process '$processName' for the procedure '$pmd'."
                )
            )

    override fun getProcessContext(cpid: Cpid, ocid: Ocid): Result<LatestProcessContext?, Fail.Incident> {
        val data = oldProcessContextRepository.load(ocid = ocid)
            .orForwardFail { fail -> return fail }
            ?: oldProcessContextRepository.load(cpid = cpid)
                .orForwardFail { fail -> return fail }

        return data
            ?.let {
                transform.tryDeserialization(it, OldProcessContext::class.java)
                    .orForwardFail { fail -> return fail }
                    .convert()
                    .orForwardFail { fail -> return fail }
            }
            .asSuccess()
    }

    override fun launchProcess(
        processDefinitionKey: ProcessDefinitionKey,
        operationId: OperationId,
        variables: Map<String, Any>
    ) {
        runtimeService.startProcessInstanceByKey(processDefinitionKey.toString(), operationId.toString(), variables)
    }

    override fun isLaunchedProcess(operationId: OperationId): Result<Boolean, Fail.Incident> =
        processInitializerRepository.isLaunchedProcess(operationId)

    private fun OldProcessContext.convert(): Result<LatestProcessContext, Fail.Incident.Bpe> {
        val cpid: Cpid = this.cpid
            .let { value ->
                Cpid.tryCreateOrNull(value)
                    ?: return failure(Fail.Incident.Bpe(description = "The old process context contain unknown value '${value}' of the 'cpid' attribute."))
            }
        val ocid: Ocid = this.ocid
            .let { value ->
                Ocid.SingleStage.tryCreateOrNull(value)
                    ?: return failure(Fail.Incident.Bpe(description = "The old process context contain unknown value '${value}' of the 'ocid' attribute."))
            }
        val stage: Stage = this.stage
            ?.let { value ->
                Stage.orNull(value)
                    ?: return failure(Fail.Incident.Bpe(description = "The old process context contain unknown value '${value}' of the 'stage' attribute."))
            }
            ?: return failure(Fail.Incident.Bpe(description = "The old process context does not contain the 'stage' attribute."))
        val phase = this.phase
            ?.let { value -> Phase(value) }
            ?: return failure(Fail.Incident.Bpe(description = "The old process context does not contain the 'phase' attribute."))
        val country = this.country
            ?: return failure(Fail.Incident.Bpe(description = "The old process context does not contain the 'country' attribute."))
        val language = this.language
            ?: return failure(Fail.Incident.Bpe(description = "The old process context does not contain the 'language' attribute."))
        val pmd = this.pmd
            ?.let { value ->
                ProcurementMethodDetails.orNull(value)
                    ?: return failure(Fail.Incident.Bpe(description = "The old process context contain unknown value '$value' of the 'pmd' attribute."))
            }
            ?: return failure(Fail.Incident.Bpe(description = "The old process context does not contain the 'pmd' attribute."))

        val owner = this.owner
            .let { value ->
                Owner.tryCreateOrNull(value)
                    ?: return failure(Fail.Incident.Bpe(description = "The old process context contain unknown value '$value' of the 'owner' attribute."))
            }

        val isAuction = this.isAuction
        val mainProcurementCategory = this.mainProcurementCategory
        val awardCriteria = this.awardCriteria
            ?.let { value ->
                AwardCriteria.orNull(value)
                    ?: return failure(Fail.Incident.Bpe(description = "The old process context contain unknown value '$value' of the 'awardCriteria' attribute."))
            }

        return LatestProcessContext(
            cpid = cpid,
            ocid = ocid,
            stage = stage,
            phase = phase,
            country = country,
            language = language,
            pmd = pmd,
            isAuction = isAuction,
            mainProcurementCategory = mainProcurementCategory,
            awardCriteria = awardCriteria,
            owner = owner
        ).asSuccess()
    }
}
