package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.application.model.Phase
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractInternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import org.springframework.stereotype.Component

@Component
class BpeSetPhaseDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractInternalDelegate<BpeSetPhaseDelegate.Parameters, BpeSetPhaseDelegate.Phases>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        private const val PARAMETER_NAME_PHASE = "value"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val phase: Phases = parameterContainer.getString(PARAMETER_NAME_PHASE)
            .orForwardFail { fail -> return fail }
            .let { phase ->
                Phases.orNull(phase)
                    ?: return Result.failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = PARAMETER_NAME_PHASE,
                            expectedValues = Phases.allowedElements.keysAsStrings(),
                            actualValue = phase
                        )
                    )
            }
        return success(Parameters(phase = phase))
    }

    override suspend fun execute(
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Option<Phases>, Fail.Incident> = success(Option.pure(parameters.phase))

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        data: Phases
    ): MaybeFail<Fail.Incident.Bpmn> {

        context.processInfo = context.processInfo
            .copy(phase = Phase(data.key))
        return MaybeFail.none()
    }

    class Parameters(val phase: Phases)

    enum class Phases(@JsonValue override val key: String) : EnumElementProvider.Key {
        AGGREGATED("aggregated"),
        AGGREGATION_PENDING("aggregationPending"),
        EMPTY("empty"),
        QUALIFICATION("qualification"),
        QUALIFICATION_STANDSTILL("qualificationStandstill"),
        SUSPENDED("suspended"),
        TENDERING("tendering");

        override fun toString(): String = key

        companion object : EnumElementProvider<Phases>(info = info()) {

            @JvmStatic
            @JsonCreator
            fun creator(name: String) = Phases.orThrow(name)
        }
    }
}
