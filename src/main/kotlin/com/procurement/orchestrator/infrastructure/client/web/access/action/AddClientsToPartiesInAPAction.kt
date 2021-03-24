package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.address.Address
import com.procurement.orchestrator.domain.model.address.AddressDetails
import com.procurement.orchestrator.domain.model.address.country.CountryDetails
import com.procurement.orchestrator.domain.model.address.locality.LocalityDetails
import com.procurement.orchestrator.domain.model.address.region.RegionDetails
import com.procurement.orchestrator.domain.model.identifier.Identifier
import com.procurement.orchestrator.domain.model.identifier.Identifiers
import com.procurement.orchestrator.domain.model.organization.ContactPoint
import com.procurement.orchestrator.domain.model.organization.datail.Details
import com.procurement.orchestrator.domain.model.organization.datail.TypeOfBuyer
import com.procurement.orchestrator.domain.model.party.Party
import com.procurement.orchestrator.domain.model.party.PartyRole
import com.procurement.orchestrator.domain.model.party.PartyRoles
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.client.web.access.action.AddClientsToPartiesInAPAction.Params
import com.procurement.orchestrator.infrastructure.client.web.access.action.AddClientsToPartiesInAPAction.Result
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class AddClientsToPartiesInAPAction : FunctionalAction<Params, Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "addClientsToPartiesInAP"
    override val target: Target<Result> = Target.single()

    class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("relatedCpid") @field:JsonProperty("relatedCpid") val relatedCpid: Cpid,
        @param:JsonProperty("relatedOcid") @field:JsonProperty("relatedOcid") val relatedOcid: Ocid
    )

    class Result(
        @param:JsonProperty("parties") @field:JsonProperty("parties") val parties: List<Party>
    ) : Serializable {

        data class Party(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
            @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: Identifier,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("additionalIdentifiers") @param:JsonProperty("additionalIdentifiers") val additionalIdentifiers: List<Identifier>?,

            @param:JsonProperty("address") @field:JsonProperty("address") val address: Address,
            @param:JsonProperty("contactPoint") @field:JsonProperty("contactPoint") val contactPoint: ContactPoint,
            @param:JsonProperty("roles") @field:JsonProperty("roles") val roles: List<PartyRole>,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("details") @field:JsonProperty("details") val details: Details?
        ) : Serializable {

            data class Identifier(
                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("legalName") @param:JsonProperty("legalName") val legalName: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
            ): Serializable

            data class Address(
                @param:JsonProperty("streetAddress") @field:JsonProperty("streetAddress") val streetAddress: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("postalCode") @field:JsonProperty("postalCode") val postalCode: String?,

                @param:JsonProperty("addressDetails") @field:JsonProperty("addressDetails") val addressDetails: AddressDetails
            ) : Serializable {

                data class AddressDetails(
                    @param:JsonProperty("country") @field:JsonProperty("country") val country: Country,
                    @param:JsonProperty("region") @field:JsonProperty("region") val region: Region,
                    @param:JsonProperty("locality") @field:JsonProperty("locality") val locality: Locality
                ) : Serializable {

                    data class Country(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                        @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String
                    ) : Serializable

                    data class Region(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                        @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String
                    ) : Serializable

                    data class Locality(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                    ) : Serializable
                }
            }

            data class ContactPoint(
                @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
                @param:JsonProperty("email") @field:JsonProperty("email") val email: String,
                @param:JsonProperty("telephone") @field:JsonProperty("telephone") val telephone: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("faxNumber") @field:JsonProperty("faxNumber") val faxNumber: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("url") @field:JsonProperty("url") val url: String?
            ): Serializable

            data class Details(
                @field:JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("typeOfBuyer") @param:JsonProperty("typeOfBuyer") val typeOfBuyer: TypeOfBuyer?,

                @field:JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("mainGeneralActivity") @param:JsonProperty("mainGeneralActivity") val mainGeneralActivity: String?,

                @field:JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("mainSectoralActivity") @param:JsonProperty("mainSectoralActivity") val mainSectoralActivity: String?
            ): Serializable
        }
    }
}

fun Result.Party.toDomain() =
    Party(
        id = id,
        name = name,
        identifier = identifier.toDomain(),
        additionalIdentifiers = additionalIdentifiers.orEmpty()
            .map { it.toDomain() }
            .let { Identifiers(it) }
        ,
        address = address.toDomain(),
        contactPoint = contactPoint.toDomain(),
        details = details?.toDomain(),
        roles = PartyRoles(roles)
    )

private fun Result.Party.Details.toDomain() =
    Details(
        typeOfBuyer = typeOfBuyer,
        mainGeneralActivity = mainGeneralActivity,
        mainSectoralActivity = mainSectoralActivity
    )

private fun Result.Party.ContactPoint.toDomain() =
    ContactPoint(
        name = name,
        email = email,
        telephone = telephone,
        faxNumber = faxNumber,
        url = url
    )

private fun Result.Party.Address.toDomain() =
    Address(
        streetAddress = streetAddress,
        postalCode = postalCode,
        addressDetails = addressDetails.let { addressDetails ->
            AddressDetails(
                country = addressDetails.country.let { country ->
                    CountryDetails(
                        id = country.id,
                        scheme = country.scheme,
                        description = country.description,
                        uri = country.uri
                    )
                },
                region = addressDetails.region.let { region ->
                    RegionDetails(
                        id = region.id,
                        scheme = region.scheme,
                        description = region.description,
                        uri = region.uri
                    )
                },
                locality = addressDetails.locality.let { locality ->
                    LocalityDetails(
                        id = locality.id,
                        scheme = locality.scheme,
                        description = locality.description,
                        uri = locality.uri
                    )
                }
            )
        }
    )


private fun Result.Party.Identifier.toDomain() =
    Identifier(
        id = id,
        scheme = scheme,
        legalName = legalName,
        uri = uri
    )
