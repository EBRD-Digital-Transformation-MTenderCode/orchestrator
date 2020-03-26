package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.error

import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.members.Errors
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractInternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import org.springframework.stereotype.Component

@Component
class BpeErrorAppenderDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractInternalDelegate<BpeErrorAppenderDelegate.Parameters, Errors.Error>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        private const val NAME_PARAMETER_OF_ERROR_CODE = "errorCode"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> =
        Parameters(
            errorCode = parameterContainer.getString(NAME_PARAMETER_OF_ERROR_CODE)
                .doOnError { return failure(it) }
                .get
        ).asSuccess()

    override suspend fun execute(
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Errors.Error, Fail.Incident> = success(Errors.Error(code = parameters.errorCode))


    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        data: Errors.Error
    ): MaybeFail<Fail.Incident.Bpmn> {
        val errors: List<Errors.Error> = context.errors ?: emptyList()
        context.errors = Errors(values = errors + data)
        return MaybeFail.none()
    }

    class Parameters(
        val errorCode: String
    )
}
