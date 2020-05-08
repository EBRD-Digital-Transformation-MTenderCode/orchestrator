package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe

import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.members.ProcessInfo
import com.procurement.orchestrator.application.model.context.members.RequestInfo
import com.procurement.orchestrator.application.model.process.OldProcessContext
import com.procurement.orchestrator.application.model.process.ProcessContext
import com.procurement.orchestrator.application.repository.OldProcessContextRepository
import com.procurement.orchestrator.application.repository.ProcessContextRepository
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.extension.date.format
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractInternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import org.springframework.stereotype.Component
import java.time.ZoneOffset

@Component
class BpeSaveContextDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    private val processContextRepository: ProcessContextRepository,
    private val oldProcessContextRepository: OldProcessContextRepository,
    private val transform: Transform
) : AbstractInternalDelegate<Unit, Unit>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Option<Unit>, Fail.Incident> {
        val requestInfo = context.requestInfo
        val processInfo = context.processInfo

        saveProcessContextRepository(requestInfo, processInfo)
            .doOnFail { return failure(it) }

        saveOldProcessContextRepository(requestInfo, processInfo)
            .doOnFail { return failure(it) }

        return success(Option.none())
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        data: Unit
    ): MaybeFail<Fail.Incident.Bpmn> = MaybeFail.none()

    private fun saveProcessContextRepository(
        requestInfo: RequestInfo,
        processInfo: ProcessInfo
    ): MaybeFail<Fail.Incident> {
        val processContext = ProcessContext(
            cpid = processInfo.cpid.toString(),
            ocid = processInfo.ocid.toString(),
            operationId = requestInfo.operationId.toString(),
            requestId = requestInfo.requestId.toString(),
            owner = requestInfo.owner.toString(),
            stage = processInfo.stage.toString(),
            prevStage = processInfo.prevStage?.toString(),
            processDefinitionKey = processInfo.processDefinitionKey.toString(),
            operationType = processInfo.operationType,
            phase = processInfo.phase.toString(),
            country = requestInfo.country,
            language = requestInfo.language,
            pmd = processInfo.pmd.toString(),
            startDate = requestInfo.timestamp,
            timestamp = requestInfo.timestamp.toInstant(ZoneOffset.UTC).toEpochMilli(),
            isAuction = processInfo.isAuction,
            mainProcurementCategory = processInfo.mainProcurementCategory,
            awardCriteria = processInfo.awardCriteria?.toString()
        )

        val serializedContext = transform.trySerialization(processContext)
            .orReturnFail { return MaybeFail.fail(it) }

        processContextRepository
            .save(
                cpid = processInfo.cpid,
                timestamp = requestInfo.timestamp,
                operationId = requestInfo.operationId,
                context = serializedContext
            )
            .doOnError { return MaybeFail.fail(it) }

        return MaybeFail.none()
    }

    private fun saveOldProcessContextRepository(
        requestInfo: RequestInfo,
        processInfo: ProcessInfo
    ): MaybeFail<Fail.Incident> {
        val oldProcessContext = OldProcessContext(
            cpid = processInfo.cpid.toString(),
            ocid = processInfo.ocid.toString(),
            operationId = requestInfo.operationId.toString(),
            requestId = requestInfo.requestId.toString(),
            owner = requestInfo.owner.toString(),
            stage = processInfo.stage.toString(),
            prevStage = processInfo.prevStage?.toString(),
            processType = processInfo.processDefinitionKey.toString(),
            operationType = processInfo.operationType.toString(),
            phase = processInfo.phase.toString(),
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

        oldProcessContextRepository.save(cpid = processInfo.cpid, context = serializedContext)
            .doOnError { return MaybeFail.fail(it) }

        return MaybeFail.none()
    }
}
