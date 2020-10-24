package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe

import com.procurement.orchestrator.application.model.Phase
import com.procurement.orchestrator.application.model.Stage
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.members.ProcessInfo
import com.procurement.orchestrator.application.model.context.members.RequestInfo
import com.procurement.orchestrator.application.model.process.OldProcessContext
import com.procurement.orchestrator.application.repository.OldProcessContextRepository
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.extension.date.format
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractInternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import org.springframework.stereotype.Component
import java.time.ZoneOffset

@Component
class BpeSaveContextDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    private val oldProcessContextRepository: OldProcessContextRepository,
    private val transform: Transform
) : AbstractInternalDelegate<BpeSaveContextDelegate.Parameters, Unit>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        private const val PARAMETER_STAGE = "stage"
        private const val PARAMETER_PHASE = "phase"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val stage = parameterContainer.getStringOrNull(PARAMETER_STAGE)
            .orForwardFail { fail -> return fail }
            ?.let {
                Stage.orNull(it)
                    ?: return failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = PARAMETER_STAGE,
                            expectedValues = Stage.allowedElements.keysAsStrings(),
                            actualValue = it
                        )
                    )
            }

        val phase = parameterContainer.getStringOrNull(PARAMETER_PHASE)
            .orForwardFail { fail -> return fail }
            ?.let { Phase(it) }

        return success(Parameters(stage, phase))
    }

    override suspend fun execute(
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Option<Unit>, Fail.Incident> {
        val requestInfo = context.requestInfo
        val processInfo = context.processInfo

        saveOldProcessContextRepository(requestInfo, processInfo, parameters)
            .doOnFail { return failure(it) }

        return success(Option.none())
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        data: Unit
    ): MaybeFail<Fail.Incident.Bpmn> = MaybeFail.none()

    private fun saveOldProcessContextRepository(
        requestInfo: RequestInfo,
        processInfo: ProcessInfo,
        parameters: Parameters
    ): MaybeFail<Fail.Incident> {
        val oldProcessContext = OldProcessContext(
            cpid = processInfo.cpid.toString(),
            ocid = processInfo.ocid.toString(),
            operationId = requestInfo.operationId.toString(),
            requestId = requestInfo.requestId.toString(),
            owner = requestInfo.owner.toString(),
            stage = parameters.stage?.key ?: processInfo.stage.toString(),
            prevStage = processInfo.prevStage?.toString(),
            processType = processInfo.processDefinitionKey.toString(),
            operationType = processInfo.operationType.toString(),
            phase = parameters.phase?.toString() ?: processInfo.phase.toString(),
            country = requestInfo.country,
            language = requestInfo.language,
            pmd = processInfo.pmd.toString(),
            startDate = requestInfo.timestamp.format(),
            timestamp = requestInfo.timestamp.toInstant(ZoneOffset.UTC).toEpochMilli(),
            isAuction = processInfo.isAuction,
            mainProcurementCategory = processInfo.mainProcurementCategory,
            awardCriteria = processInfo.awardCriteria?.toString()
        )

        val serializedContext = transform.trySerialization(oldProcessContext)
            .orReturnFail { return MaybeFail.fail(it) }

        val ocid = processInfo.ocid as? Ocid.SingleStage

        when (ocid?.stage ?: processInfo.stage) {
            Stage.PC -> oldProcessContextRepository.save(ocid = processInfo.ocid, context = serializedContext)
                .doOnError { return MaybeFail.fail(it) }
            Stage.EI,
            Stage.FS,
            Stage.PN,
            Stage.AP,
            Stage.EV,
            Stage.FE,
            Stage.NP,
            Stage.TP,
            Stage.AC -> oldProcessContextRepository.save(cpid = processInfo.cpid, context = serializedContext)
                .doOnError { return MaybeFail.fail(it) }
        }

        return MaybeFail.none()
    }

    class Parameters(val stage: Stage?, val phase: Phase?)
}
