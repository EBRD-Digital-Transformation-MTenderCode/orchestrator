package com.procurement.orchestrator.infrastructure.bpms.delegate.mdm

import com.procurement.orchestrator.application.client.MdmClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.model.context.members.Errors
import com.procurement.orchestrator.application.model.context.members.Incident
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.extension.date.format
import com.procurement.orchestrator.domain.extension.date.nowDefaultUTC
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.classification.Classification
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractBatchRestDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichGeneratedTenderClassificationAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetGeneratedTenderClassification
import com.procurement.orchestrator.infrastructure.configuration.property.GlobalProperties
import org.springframework.stereotype.Component

@Component
class MdmEnrichGeneratedTenderClassificationDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    transform: Transform,
    private val mdmClient: MdmClient
) : AbstractBatchRestDelegate<Unit, EnrichGeneratedTenderClassificationAction.Params, List<Classification>>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {
    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        Unit.asSuccess()

    override fun prepareSeq(
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<List<EnrichGeneratedTenderClassificationAction.Params>, Fail.Incident> {
        val requestInfo = context.requestInfo

        val tender = context.tryGetTender()
            .orForwardFail { incident -> return incident }

        val params = listOf(
            EnrichGeneratedTenderClassificationAction.Params(
                classificationId = tender.classification!!.id,
                lang = requestInfo.language,
                scheme = tender.classification.scheme
            )
        )

        return Result.success(params)
    }

    override suspend fun execute(
        elements: List<EnrichGeneratedTenderClassificationAction.Params>,
        executionInterceptor: ExecutionInterceptor
    ): Result<List<Classification>, Fail.Incident> {

        return elements
            .map { params ->
                mdmClient
                    .enrichGeneratedTenderClassification(params = params)
                    .orForwardFail { error -> return error }
                    .let { result -> handleResult(result, executionInterceptor) }
            }.filter { optionalResult -> optionalResult.isDefined }
            .map { result -> result.get }
            .asSuccess()
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: List<Classification>
    ): MaybeFail<Fail.Incident> {

        val tender = context.tryGetTender()
            .orReturnFail { return MaybeFail.fail(it) }

        val updatedTender = tender.copy(
            classification = result.firstOrNull()
        )

        context.tender = updatedTender

        return MaybeFail.none()
    }

    private fun handleResult(
        result: GetGeneratedTenderClassification.Result,
        executionInterceptor: ExecutionInterceptor
    ): Option<Classification> = when (result) {
        is GetGeneratedTenderClassification.Result.Success -> Option.pure(
            Classification(id = result.id, scheme = result.scheme, description = result.description)
        )
        is GetGeneratedTenderClassification.Result.Fail.SchemeNotFound -> Option.none()
        is GetGeneratedTenderClassification.Result.Fail.IdNotFound -> {
            val errors = result.details.errors.convertErrors()
            executionInterceptor.throwError(errors = errors)
        }
        is GetGeneratedTenderClassification.Result.Fail.LanguageNotFound -> {
            val errors = result.details.errors.convertErrors()
            executionInterceptor.throwError(errors = errors)
        }
        is GetGeneratedTenderClassification.Result.Fail.AnotherError -> {
            val errors = result.details.errors.convertErrors()
            executionInterceptor.throwError(errors = errors)
        }
        is GetGeneratedTenderClassification.Result.Fail.TranslationNotFound -> {
            executionInterceptor.throwIncident(
                Incident(
                    id = executionInterceptor.processInstanceId,
                    date = nowDefaultUTC().format(),
                    level = Fail.Incident.Level.ERROR.toString(),
                    service = GlobalProperties.service
                        .let { service ->
                            Incident.Service(
                                id = service.id,
                                name = service.name,
                                version = service.version
                            )
                        },
                    details = result.details.errors
                        .map { incident ->
                            Incident.Detail(
                                code = incident.code,
                                description = incident.description
                            )
                        }
                )
            )
        }
    }

    private fun List<EnrichGeneratedTenderClassificationAction.Response.Error.Details>.convertErrors(): List<Errors.Error> =
        this.map { error ->
            Errors.Error(code = error.code, description = error.description)
        }
}