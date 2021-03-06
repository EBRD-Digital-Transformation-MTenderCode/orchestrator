package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.domain.model.tender.conversion.Conversion
import com.procurement.orchestrator.domain.model.tender.conversion.Conversions
import com.procurement.orchestrator.domain.model.tender.conversion.coefficient.Coefficient
import com.procurement.orchestrator.domain.model.tender.conversion.coefficient.Coefficients
import com.procurement.orchestrator.domain.model.tender.criteria.OtherCriteria
import com.procurement.orchestrator.domain.model.tender.criteria.QualificationSystemMethods
import com.procurement.orchestrator.domain.util.extension.getNewElements
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.AccessCommands
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetQualificationCriteriaAndMethodAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class AccessGetQualificationCriteriaAndMethodDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, GetQualificationCriteriaAndMethodAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<GetQualificationCriteriaAndMethodAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        return accessClient.getQualificationCriteriaAndMethod(
            id = commandId,
            params = GetQualificationCriteriaAndMethodAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<GetQualificationCriteriaAndMethodAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.ACCESS,
                    action = AccessCommands.GetQualificationCriteriaAndMethod
                )
            )

        val tender = context.tender ?: Tender()

        val receivedConversion = data.conversions
            .orEmpty()
            .map { it.convertToContextEntity() }

        val receivedConversionsIds = receivedConversion
            .map { it.id }

        val availableConversionsDbIds = tender.conversions
            .map { it.id }

        val receivedConversionsById = receivedConversion
            .associateBy { it.id }

        val updatedConversions = tender.conversions
            .map { conversion ->
                receivedConversionsById[conversion.id]
                    ?.let { rqConversion -> conversion.updateBy(rqConversion) }
                    ?: conversion
            }

        val newConversionIds = getNewElements(received = receivedConversionsIds, known = availableConversionsDbIds)
        val newConversion = newConversionIds
            .map { conversionId -> receivedConversionsById.getValue(conversionId) }

        val otherCriteria = OtherCriteria(
            reductionCriteria = data.reductionCriteria,
            qualificationSystemMethods = QualificationSystemMethods(data.qualificationSystemMethods)
        )

        val updatedTender = tender.copy(
            otherCriteria = otherCriteria,
            conversions = Conversions(updatedConversions + newConversion)
        )

        context.tender = updatedTender

        return MaybeFail.none()
    }


    private fun GetQualificationCriteriaAndMethodAction.Result.Conversion.convertToContextEntity(): Conversion {

        val convertedCoefficient = this.coefficients
            .map { it.convertToContextEntity() }

        return Conversion(
            id           = this.id,
            description  = this.description,
            relatesTo    = this.relatesTo,
            relatedItem  = this.relatedItem,
            rationale    = this.rationale,
            coefficients = Coefficients(convertedCoefficient)
        )
    }

    private fun GetQualificationCriteriaAndMethodAction.Result.Conversion.Coefficient.convertToContextEntity(): Coefficient =
        Coefficient(
            id          = this.id,
            coefficient = this.coefficient,
            value       = this.value
        )
}
