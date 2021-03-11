package com.procurement.orchestrator.infrastructure.client.web.submission.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.award.Award
import com.procurement.orchestrator.domain.model.bid.Bid
import com.procurement.orchestrator.domain.model.bid.BidId
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.document.DocumentType
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.lot.RelatedLots
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.client.web.submission.action.FindDocumentsByBidIdsAction.Params
import com.procurement.orchestrator.infrastructure.client.web.submission.action.FindDocumentsByBidIdsAction.Result
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class FindDocumentsByBidIdsAction : FunctionalAction<Params, Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "findDocumentsByBidIds"
    override val target: Target<Result> = Target.single()

    class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("bids") @field:JsonProperty("bids") val bids: Bids
    ) {
        class Bids(
            @param:JsonProperty("details") @field:JsonProperty("details") val details: List<BidDetails>
        ) { companion object {}

            class BidDetails(
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String?
            ) { companion object {} }
        }
    }

    class Result(
        @param:JsonProperty("bids") @field:JsonProperty("bids") val bids: Bids
    ) : Serializable {

        data class Bids(
            @param:JsonProperty("details") @field:JsonProperty("details") val details: List<Detail>
        ) : Serializable {

            data class Detail(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: BidId,
                @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>
            ) : Serializable {

                data class Document(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                    @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: String,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @param:JsonProperty("relatedLots") @field:JsonProperty("relatedLots") val relatedLots: List<String>?
                ) : Serializable

            }
        }
    }
}

fun Params.Bids.Companion.fromDomain(award: Award) =
    Params.Bids(
        details = listOf(
            Params.Bids.BidDetails(id = award.relatedBid)
        )
    )

fun Result.Bids.Detail.toDomain() =
    Bid(
        id = this.id,
        documents = this.documents
            .map { it.toDomain() }
            .let { Documents(it) }
    )

fun Result.Bids.Detail.Document.toDomain() =
    Document(
        id = DocumentId.create(this.id),
        title = this.title,
        description = this.description,
        documentType = DocumentType.creator(this.documentType),
        relatedLots = this.relatedLots.orEmpty()
            .map { LotId.create(it) }
            .let { RelatedLots(it) }
    )
