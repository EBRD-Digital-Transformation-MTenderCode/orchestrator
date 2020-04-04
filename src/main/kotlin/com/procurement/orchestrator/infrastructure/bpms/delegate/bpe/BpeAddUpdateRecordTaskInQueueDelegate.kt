package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.extension.date.nowDefaultUTC
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.award.Award
import com.procurement.orchestrator.domain.model.bid.Bids
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.NoticeQueueRepository
import com.procurement.orchestrator.infrastructure.bpms.repository.NoticeTask
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import org.springframework.stereotype.Component

@Component
class BpeAddUpdateRecordTaskInQueueDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    private val noticeQueueRepository: NoticeQueueRepository,
    private val transform: Transform
) : AbstractExternalDelegate<Unit, Unit>(
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
    ): Result<Reply<Unit>, Fail.Incident> {

        val processInfo = context.processInfo
        val cpid = processInfo.cpid
        val ocid = processInfo.ocid
        val data = transform
            .trySerialization(
                TaskData(
                    cpid = cpid,
                    ocid = ocid,
                    tender = context.tender,
                    bids = context.bids,
                    awards = context.awards
                )
            )
            .orReturnFail { return failure(it) }

        val requestInfo = context.requestInfo
        noticeQueueRepository
            .save(
                task = NoticeTask(
                    operationId = requestInfo.operationId,
                    timestamp = nowDefaultUTC(),
                    cpid = cpid,
                    ocid = ocid,
                    action = NoticeTask.Action.UPDATE_RECORD,
                    data = data
                )
            )
            .doOnError { return failure(it) }

        return success(Reply.None)
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        data: Unit
    ): MaybeFail<Fail.Incident.Bpmn> = MaybeFail.none()

    data class TaskData(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender?,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("bids") @param:JsonProperty("bids") val bids: Bids?,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("awards") @param:JsonProperty("awards") val awards: List<Award>
    )
}
