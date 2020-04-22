package com.procurement.orchestrator.infrastructure.bpms.model.queue

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.Stage
import com.procurement.orchestrator.application.model.context.members.Awards
import com.procurement.orchestrator.application.model.context.members.Contracts
import com.procurement.orchestrator.application.model.context.members.Parties
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.bind
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.bid.Bids
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.domain.model.updateBy
import java.time.LocalDateTime

class QueueNoticeTask(
    val id: Id,
    val timestamp: LocalDateTime,
    val data: String
) : Comparable<QueueNoticeTask> {

    companion object : Comparator<QueueNoticeTask> {
        override fun compare(current: QueueNoticeTask, other: QueueNoticeTask): Int = current.compareTo(other)
    }

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is QueueNoticeTask
            && this.id == other.id
            && this.timestamp == other.timestamp

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + timestamp.hashCode()
        return result
    }

    override fun compareTo(other: QueueNoticeTask): Int {
        val result = id.compareTo(other.id)
        return if (result != 0) result else timestamp.compareTo(other.timestamp)
    }

    class Id(
        val cpid: Cpid,
        val ocid: Ocid,
        val action: Action
    ) : Comparable<Id> {

        override fun equals(other: Any?): Boolean = if (this === other)
            true
        else
            other is Id
                && this.cpid == other.cpid
                && this.ocid == other.ocid
                && this.action == other.action

        override fun hashCode(): Int {
            var result = cpid.hashCode()
            result = 31 * result + ocid.hashCode()
            result = 31 * result + action.hashCode()
            return result
        }

        override fun compareTo(other: Id): Int {
            var result = cpid.compareTo(other.cpid)
            result = if (result != 0) result else Stage.compare(current = ocid.stage, other = other.ocid.stage)
            result = if (result != 0) result else Action.compare(current = action, other = other.action)
            return result
        }
    }

    data class Data(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender?,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("bids") @param:JsonProperty("bids") val bids: Bids?,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("awards") @param:JsonProperty("awards") val awards: Awards,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("parties") @param:JsonProperty("parties") val parties: Parties,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("parties") @param:JsonProperty("parties") val contracts: Contracts
    )

    enum class Action(override val key: String, private val weight: Int) : EnumElementProvider.Key {

        CREATE_RECORD("createRecord", weight = 10),
        UPDATE_RECORD("updateRecord", weight = 20);

        override fun toString(): String = key

        companion object : EnumElementProvider<Action>(info = info()), Comparator<Action> {
            override fun compare(current: Action, other: Action): Int = current.weight.compareTo(other.weight)
        }
    }
}

fun List<QueueNoticeTask>.grouping(): Map<QueueNoticeTask.Id, List<QueueNoticeTask>> =
    this.asSequence()
        .sortedWith(QueueNoticeTask)
        .groupBy { task -> task.id }

fun List<QueueNoticeTask>.merge(transform: Transform): Result<String, Fail.Incident.Transform> {

    tailrec fun mergeData(
        data: QueueNoticeTask.Data? = null,
        tasks: List<QueueNoticeTask>,
        index: Int = 0
    ): Result<QueueNoticeTask.Data?, Fail.Incident.Transform> {
        return if (index < tasks.size) {
            val currentData = tasks[index]
                .let { task ->
                    transform.tryDeserialization(task.data, QueueNoticeTask.Data::class.java)
                        .orForwardFail { return it }
                }
            val updatedData = data?.update(currentData) ?: currentData
            mergeData(updatedData, tasks, index + 1)
        } else
            Result.success(data)
    }

    return mergeData(tasks = this)
        .bind { data -> transform.trySerialization(data) }
}

private fun QueueNoticeTask.Data.update(data: QueueNoticeTask.Data): QueueNoticeTask.Data = QueueNoticeTask.Data(
    cpid = cpid,
    ocid = ocid,
    tender = tender updateBy data.tender,
    bids = bids updateBy data.bids,
    awards = awards updateBy data.awards,
    parties = parties updateBy data.parties,
    contracts = contracts updateBy data.contracts
)
