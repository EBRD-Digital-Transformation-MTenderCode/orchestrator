package com.procurement.orchestrator.infrastructure.bpms.delegate.mdm

import com.procurement.orchestrator.application.client.MdmClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.model.context.members.RequestInfo
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.mdm.Mdm
import com.procurement.orchestrator.domain.model.mdm.ProcessMasterData
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.domain.model.tender.criteria.Criteria
import com.procurement.orchestrator.domain.model.tender.criteria.Criterion
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractBatchRestDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetStandardCriteria
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetStandardCriteriaAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.convertToGlobalContextEntity
import org.springframework.stereotype.Component

@Component
class MdmGetStandartCriteriaDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    transform: Transform,
    private val mdmClient: MdmClient
) : AbstractBatchRestDelegate<MdmGetStandartCriteriaDelegate.Parameters, GetStandardCriteriaAction.Params, List<Criterion>>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        const val PARAMETER_CRITERIA_CATEGORY = "criteriaCategory"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val criteriaCategories = parameterContainer.getListString(PARAMETER_CRITERIA_CATEGORY)
            .orForwardFail { fail -> return fail }
            .map {
                CriteriaCategory.orNull(it)
                    ?: return Result.failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = PARAMETER_CRITERIA_CATEGORY,
                            expectedValues = CriteriaCategory.allowedElements.keysAsStrings(),
                            actualValue = it
                        )
                    )
            }
        return Parameters(criteriaCategories).asSuccess()
    }

    override fun prepareSeq(
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<List<GetStandardCriteriaAction.Params>, Fail.Incident> {
        val requestInfo = context.requestInfo
        val tender = context.tryGetTender()
            .orForwardFail { return it }

        return if (parameters.criteriaCategories.isEmpty())
            generateParams(requestInfo, tender)
        else generateParamsWithCriteria(parameters, requestInfo, tender)
    }

    private fun generateParams(
        requestInfo: RequestInfo,
        tender: Tender
    ): Result<List<GetStandardCriteriaAction.Params>, Fail.Incident> =
        GetStandardCriteriaAction.Params(
            lang = requestInfo.language,
            country = requestInfo.country,
            mainProcurementCategory = tender.mainProcurementCategory,
            criteriaCategory = null
        )
            .let { listOf(it) }
            .asSuccess()

    private fun generateParamsWithCriteria(
        parameters: Parameters,
        requestInfo: RequestInfo,
        tender: Tender
    ): Result<List<GetStandardCriteriaAction.Params>, Fail.Incident> =
        parameters.criteriaCategories
            .map { criteriaCategory ->
                GetStandardCriteriaAction.Params(
                    lang = requestInfo.language,
                    country = requestInfo.country,
                    mainProcurementCategory = tender.mainProcurementCategory,
                    criteriaCategory = criteriaCategory.key
                )
            }.asSuccess()

    override suspend fun execute(
        elements: List<GetStandardCriteriaAction.Params>,
        executionInterceptor: ExecutionInterceptor
    ): Result<Criteria, Fail.Incident> =
        elements.flatMap { params ->
            mdmClient.getStandardCriteria(params = params)
                .orForwardFail { error -> return error }
                .let { result -> handleResult(result) }
        }
            .let { Criteria(it) }
            .asSuccess()


    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        result: List<Criterion>
    ): MaybeFail<Fail.Incident> {
        if (result.isEmpty())
            return MaybeFail.none()

        context.processMasterData = ProcessMasterData(Mdm(Criteria(generateCriteria(result))))

        return MaybeFail.none()
    }

    private fun generateCriteria(result: List<Criterion>) =
        result.map { criterion ->
            Criterion(
                id = criterion.id,
                classification = criterion.classification
            )
        }

    private fun handleResult(result: GetStandardCriteria.Result.Success): List<Criterion> =
        result.criteria.map { criterion -> criterion.convertToGlobalContextEntity() }

    data class Parameters(val criteriaCategories: List<CriteriaCategory>)

    enum class CriteriaCategory(override val key: String) : EnumElementProvider.Key {
        EXCLUSION("exclusion"),
        SELECTION("selection"),
        OTHER("other");

        override fun toString(): String = key

        companion object : EnumElementProvider<CriteriaCategory>(info = info())
    }
}

