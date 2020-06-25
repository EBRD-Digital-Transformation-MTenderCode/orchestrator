package com.procurement.orchestrator.infrastructure.bpms.delegate.mdm

import com.procurement.orchestrator.application.client.MdmClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.tender.criteria.Criteria
import com.procurement.orchestrator.domain.model.tender.criteria.Criterion
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractSingleRestDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetCriteria
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetCriteriaAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.convertToGlobalContextEntity
import org.springframework.stereotype.Component

@Component
class MdmGetCriteriaDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    transform: Transform,
    private val mdmClient: MdmClient
) : AbstractSingleRestDelegate<Unit, List<Criterion>>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(
        parameters: Unit,
        context: CamundaGlobalContext,
        executionInterceptor: ExecutionInterceptor
    ): Result<List<Criterion>, Fail.Incident> {

        val requestInfo = context.requestInfo
        val processInfo = context.processInfo

        val requestParams = GetCriteriaAction.Params(
            lang = requestInfo.language,
            country = requestInfo.country,
            pmd = processInfo.pmd,
            phase = processInfo.phase
        )
        return mdmClient.getCriteria(params = requestParams)
            .orForwardFail { error -> return error }
            .let { handleResult(it) }
            .asSuccess()
    }

    override fun updateGlobalContext(context: CamundaGlobalContext, result: List<Criterion>): MaybeFail<Fail.Incident> {

        if (result.isEmpty())
            return MaybeFail.none()

        val tender = context.tryGetTender()
            .orReturnFail { error -> return MaybeFail.fail(error) }

        context.tender = tender.copy(criteria = Criteria(result))

        return MaybeFail.none()
    }

    private fun handleResult(response: GetCriteria.Result): List<Criterion> = when (response) {
        is GetCriteria.Result.Success ->
            response.criteria.map { it.convertToGlobalContextEntity() }
    }
}

