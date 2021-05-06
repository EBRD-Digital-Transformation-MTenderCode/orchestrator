package com.procurement.orchestrator.infrastructure.client.web.contracting.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.contract.ContractId
import com.procurement.orchestrator.domain.model.contract.confirmation.response.ConfirmationResponseId
import com.procurement.orchestrator.domain.model.contract.confirmation.response.ConfirmationResponseType
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.document.DocumentType
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctionType
import com.procurement.orchestrator.domain.model.person.PersonId
import com.procurement.orchestrator.infrastructure.model.Version
import java.time.LocalDateTime

abstract class ValidateConfirmationResponseDataAction : ProceduralAction<ValidateConfirmationResponseDataAction.Params> {

    override val version: Version = Version.parse("2.0.0")
    override val name: String = "validateConfirmationResponseData"

    data class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("contracts") @field:JsonProperty("contracts") val contracts: List<Contract>
    ) {
        data class Contract(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: ContractId,
            @param:JsonProperty("confirmationResponses") @field:JsonProperty("confirmationResponses") val confirmationResponses: List<ConfirmationResponse>
        ) {
            data class ConfirmationResponse(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: ConfirmationResponseId,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("requestId") @field:JsonProperty("requestId") val requestId: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("type") @field:JsonProperty("type") val type: ConfirmationResponseType?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("value") @field:JsonProperty("value") val value: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("relatedPerson") @field:JsonProperty("relatedPerson") val relatedPerson: RelatedPerson?
            ) {
                data class RelatedPerson(
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("title") @field:JsonProperty("title") val title: String?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier?,

                    @param:JsonProperty("businessFunctions") @field:JsonProperty("businessFunctions") val businessFunctions: List<BusinessFunction>,
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: PersonId
                ) {
                    data class Identifier(
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                    )

                    data class BusinessFunction(
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("type") @field:JsonProperty("type") val type: BusinessFunctionType?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("jobTitle") @field:JsonProperty("jobTitle") val jobTitle: String?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("period") @field:JsonProperty("period") val period: Period?,

                        @JsonInclude(JsonInclude.Include.NON_EMPTY)
                        @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>?
                    ) {
                        data class Period(
                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime?
                        )

                        data class Document(
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: DocumentId,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: DocumentType?,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("title") @field:JsonProperty("title") val title: String?,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("description") @field:JsonProperty("description") val description: String?
                        )
                    }
                }
            }
        }
    }
}