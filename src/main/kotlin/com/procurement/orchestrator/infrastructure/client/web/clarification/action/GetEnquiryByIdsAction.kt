package com.procurement.orchestrator.infrastructure.client.web.clarification.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.address.country.CountryId
import com.procurement.orchestrator.domain.model.address.locality.LocalityId
import com.procurement.orchestrator.domain.model.address.region.RegionId
import com.procurement.orchestrator.domain.model.enquiry.EnquiryId
import com.procurement.orchestrator.domain.model.identifier.IdentifierId
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.organization.datail.Scale
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime

abstract class GetEnquiryByIdsAction : FunctionalAction<GetEnquiryByIdsAction.Params, GetEnquiryByIdsAction.Result> {

    override val version: Version = Version.parse("2.0.0")
    override val name: String = "getEnquiryByIds"
    override val target: Target<Result> = Target.plural()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("enquiryIds") @get:JsonProperty("enquiryIds") val enquiryIds: List<EnquiryId>
    )

    class Result(enquiry: List<Enquiry>) : List<Result.Enquiry> by enquiry, Serializable {

        class Enquiry(
            @field:JsonProperty("author") @param:JsonProperty("author") val author: Author,
            @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
            @field:JsonProperty("id") @param:JsonProperty("id") val id: EnquiryId,
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("dateAnswer") @param:JsonProperty("dateAnswer") val dateAnswer: LocalDateTime?,
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("answer") @param:JsonProperty("answer") val answer: String?,
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("relatedLot") @param:JsonProperty("relatedLot") val relatedLot: LotId?

        ) : Serializable {
            data class Author(
                @field:JsonProperty("address") @param:JsonProperty("address") val address: Address,
                @field:JsonProperty("contactPoint") @param:JsonProperty("contactPoint") val contactPoint: ContactPoint,
                @field:JsonProperty("details") @param:JsonProperty("details") val details: Details,
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: Identifier,
                @field:JsonProperty("name") @param:JsonProperty("name") val name: String,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @field:JsonProperty("additionalIdentifiers") @param:JsonProperty("additionalIdentifiers") val additionalIdentifiers: List<AdditionalIdentifier>?
            ) : Serializable {
                data class AdditionalIdentifier(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                    @field:JsonProperty("legalName") @param:JsonProperty("legalName") val legalName: String,
                    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
                ) : Serializable

                data class Address(
                    @field:JsonProperty("addressDetails") @param:JsonProperty("addressDetails") val addressDetails: AddressDetails,
                    @field:JsonProperty("streetAddress") @param:JsonProperty("streetAddress") val streetAddress: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("postalCode") @param:JsonProperty("postalCode") val postalCode: String?
                ) : Serializable {
                    data class AddressDetails(
                        @field:JsonProperty("country") @param:JsonProperty("country") val country: Country,
                        @field:JsonProperty("locality") @param:JsonProperty("locality") val locality: Locality,
                        @field:JsonProperty("region") @param:JsonProperty("region") val region: Region
                    ) : Serializable {

                        data class Country(
                            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                            @field:JsonProperty("id") @param:JsonProperty("id") val id: CountryId,
                            @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                            @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String
                        ) : Serializable

                        data class Locality(
                            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                            @field:JsonProperty("id") @param:JsonProperty("id") val id: LocalityId,
                            @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
                        ) : Serializable

                        data class Region(
                            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                            @field:JsonProperty("id") @param:JsonProperty("id") val id: RegionId,
                            @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                            @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String
                        ) : Serializable
                    }
                }

                data class ContactPoint(
                    @field:JsonProperty("email") @param:JsonProperty("email") val email: String,
                    @field:JsonProperty("name") @param:JsonProperty("name") val name: String,
                    @field:JsonProperty("telephone") @param:JsonProperty("telephone") val telephone: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("faxNumber") @param:JsonProperty("faxNumber") val faxNumber: String?,
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("url") @param:JsonProperty("url") val url: String?
                ) : Serializable

                data class Details(
                    @field:JsonProperty("scale") @param:JsonProperty("scale") val scale: Scale
                ) : Serializable

                data class Identifier(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: IdentifierId,
                    @field:JsonProperty("legalName") @param:JsonProperty("legalName") val legalName: String,
                    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
                ) : Serializable
            }
        }
    }
}
