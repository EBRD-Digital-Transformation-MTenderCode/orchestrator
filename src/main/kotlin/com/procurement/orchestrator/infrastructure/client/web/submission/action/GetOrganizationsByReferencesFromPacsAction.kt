package com.procurement.orchestrator.infrastructure.client.web.submission.action

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
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.document.DocumentType
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.identifier.Identifier
import com.procurement.orchestrator.domain.model.identifier.Identifiers
import com.procurement.orchestrator.domain.model.organization.ContactPoint
import com.procurement.orchestrator.domain.model.organization.datail.Details
import com.procurement.orchestrator.domain.model.organization.datail.MainEconomicActivities
import com.procurement.orchestrator.domain.model.organization.datail.MainEconomicActivity
import com.procurement.orchestrator.domain.model.organization.datail.Scale
import com.procurement.orchestrator.domain.model.organization.datail.TypeOfSupplier
import com.procurement.orchestrator.domain.model.organization.datail.account.AccountIdentifier
import com.procurement.orchestrator.domain.model.organization.datail.account.AccountIdentifiers
import com.procurement.orchestrator.domain.model.organization.datail.account.BankAccount
import com.procurement.orchestrator.domain.model.organization.datail.account.BankAccounts
import com.procurement.orchestrator.domain.model.organization.datail.legalform.LegalForm
import com.procurement.orchestrator.domain.model.organization.datail.permit.Issue
import com.procurement.orchestrator.domain.model.organization.datail.permit.Permit
import com.procurement.orchestrator.domain.model.organization.datail.permit.PermitDetails
import com.procurement.orchestrator.domain.model.organization.datail.permit.Permits
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunction
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctionId
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctionType
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctions
import com.procurement.orchestrator.domain.model.party.Party
import com.procurement.orchestrator.domain.model.party.PartyRole
import com.procurement.orchestrator.domain.model.party.PartyRoles
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.person.Person
import com.procurement.orchestrator.domain.model.person.PersonId
import com.procurement.orchestrator.domain.model.person.Persons
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.client.web.submission.action.GetOrganizationsByReferencesFromPacsAction.Params
import com.procurement.orchestrator.infrastructure.client.web.submission.action.GetOrganizationsByReferencesFromPacsAction.Result
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime

abstract class GetOrganizationsByReferencesFromPacsAction : FunctionalAction<Params, Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "getOrganizationsByReferencesFromPacs"
    override val target: Target<Result> = Target.single()

    data class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("parties") @field:JsonProperty("parties") val parties: List<Party>
    ) {
        data class Party(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String
        )
    }

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

            @param:JsonProperty("details") @field:JsonProperty("details") val details: Details,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("persones") @field:JsonProperty("persones") val persons: List<Person>?

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
                @field:JsonProperty("typeOfSupplier") @param:JsonProperty("typeOfSupplier") val typeOfSupplier: TypeOfSupplier?,

                @field:JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("mainEconomicActivities") @param:JsonProperty("mainEconomicActivities") val mainEconomicActivities: List<EconomicActivity>?,

                @param:JsonProperty("scale") @field:JsonProperty("scale") val scale: Scale,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("permits") @field:JsonProperty("permits") val permits: List<Permit>?,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("bankAccounts") @field:JsonProperty("bankAccounts") val bankAccounts: List<BankAccount>?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("legalForm") @field:JsonProperty("legalForm") val legalForm: LegalForm?

            ): Serializable {

                data class EconomicActivity(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                ): Serializable

                data class Permit(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("url") @field:JsonProperty("url") val url: String?,

                    @param:JsonProperty("permitDetails") @field:JsonProperty("permitDetails") val permitDetails: PermitDetails
                ): Serializable {
                    data class PermitDetails(
                        @param:JsonProperty("issuedBy") @field:JsonProperty("issuedBy") val issuedBy: IssuedBy,
                        @param:JsonProperty("issuedThought") @field:JsonProperty("issuedThought") val issuedThought: IssuedThought,
                        @param:JsonProperty("validityPeriod") @field:JsonProperty("validityPeriod") val validityPeriod: ValidityPeriod
                    ): Serializable {
                        data class IssuedBy(
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                            @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                        ): Serializable

                        data class IssuedThought(
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                            @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                        ): Serializable

                        data class ValidityPeriod(
                            @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime?
                        ): Serializable
                    }
                }

                data class BankAccount(
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                    @param:JsonProperty("bankName") @field:JsonProperty("bankName") val bankName: String,
                    @param:JsonProperty("address") @field:JsonProperty("address") val address: Address,
                    @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier,
                    @param:JsonProperty("accountIdentification") @field:JsonProperty("accountIdentification") val accountIdentification: AccountIdentification,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @param:JsonProperty("additionalAccountIdentifiers") @field:JsonProperty("additionalAccountIdentifiers") val additionalAccountIdentifiers: List<AccountIdentification>?
                ): Serializable {

                    data class Identifier(
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                    ): Serializable

                    data class AccountIdentification(
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                    ): Serializable
                }

                data class LegalForm(
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                ): Serializable
            }

            data class Person(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: PersonId,
                @param:JsonProperty("title") @field:JsonProperty("title") val title: String,
                @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
                @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier,
                @param:JsonProperty("businessFunctions") @field:JsonProperty("businessFunctions") val businessFunctions: List<BusinessFunction>
            ) : Serializable {

                data class Identifier(
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                ) : Serializable

                data class BusinessFunction(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: BusinessFunctionId,
                    @param:JsonProperty("type") @field:JsonProperty("type") val type: BusinessFunctionType,
                    @param:JsonProperty("jobTitle") @field:JsonProperty("jobTitle") val jobTitle: String,
                    @param:JsonProperty("period") @field:JsonProperty("period") val period: Period,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>?
                ) : Serializable {

                    data class Period(
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime?
                    ) : Serializable

                    data class Document(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: DocumentId,
                        @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: DocumentType,
                        @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("description") @field:JsonProperty("description") val description: String?
                    ) : Serializable
                }
            }
        }
    }
}

fun Result.Party.toDomain(): Party {
    return Party(
        id = id,
        name = name,
        identifier = identifier.toDomain(),
        additionalIdentifiers = additionalIdentifiers.orEmpty()
            .map { it.toDomain() }
            .let { Identifiers(it) }
        ,
        address = address.toDomain(),
        contactPoint = contactPoint.toDomain(),
        persons = persons.orEmpty()
            .map { it.toDomain() }
            .let { Persons(it) }
        ,
        details = details.toDomain(),
        roles = PartyRoles(roles)
    )
}

fun Result.Party.Identifier.toDomain(): Identifier {
    return Identifier(
        id = id,
        legalName = legalName,
        scheme = scheme,
        uri = uri
    )
}

fun Result.Party.Address.toDomain() =
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

fun Result.Party.ContactPoint.toDomain() =
    ContactPoint(
        name = name,
        email = email,
        telephone = telephone,
        faxNumber = faxNumber,
        url = url
    )

fun Result.Party.Person.toDomain() =
    Person(
        id = id,
        name = name,
        identifier = identifier.toDomain(),
        title = title,
        businessFunctions = businessFunctions
            .map { it.toDomain() }
            .let { BusinessFunctions(it) }
    )

fun Result.Party.Person.Identifier.toDomain(): Identifier {
    return Identifier(
        id = id,
        scheme = scheme,
        uri = uri
    )
}

fun Result.Party.Person.BusinessFunction.toDomain(): BusinessFunction {
    return BusinessFunction(
        id = id,
        type = type,
        jobTitle = jobTitle,
        period = period.toDomain(),
        documents = documents.orEmpty()
            .map { it.toDomain() }
            .let { Documents(it) }
    )
}

fun Result.Party.Person.BusinessFunction.Period.toDomain() = Period(startDate = startDate)

fun Result.Party.Person.BusinessFunction.Document.toDomain(): Document {
    return Document(
        id = id,
        title = title,
        description = description,
        documentType = documentType
    )
}

fun Result.Party.Details.toDomain() =
    Details(
        typeOfSupplier = typeOfSupplier,
        mainEconomicActivities = mainEconomicActivities.orEmpty()
            .map { it.toDomain() }
            .let { MainEconomicActivities(it) }
        ,
        scale = scale,
        permits = permits.orEmpty()
            .map { it.toDomain() }
            .let { Permits(it) }
        ,
        bankAccounts = bankAccounts.orEmpty()
            .map { it.toDomain() }
            .let { BankAccounts(it) }
        ,
        legalForm = legalForm?.toDomain()
    )

fun Result.Party.Details.EconomicActivity.toDomain(): MainEconomicActivity =
    MainEconomicActivity(
        scheme = scheme,
        id = id,
        description = description,
        uri = uri
    )

fun Result.Party.Details.Permit.toDomain(): Permit =
    Permit(
        id = id,
        scheme = scheme,
        url = url,
        permitDetails = permitDetails.toDomain()
    )

fun Result.Party.Details.Permit.PermitDetails.toDomain(): PermitDetails =
    PermitDetails(
        issuedBy = issuedBy.toDomain(),
        issuedThought = issuedThought.toDomain(),
        validityPeriod = validityPeriod.toDomain()
    )

fun Result.Party.Details.Permit.PermitDetails.IssuedBy.toDomain(): Issue = Issue(id = id, name = name)
fun Result.Party.Details.Permit.PermitDetails.IssuedThought.toDomain(): Issue = Issue(id = id, name = name)

fun Result.Party.Details.Permit.PermitDetails.ValidityPeriod.toDomain(): Period =
    Period(
        startDate = startDate,
        endDate = endDate
    )

fun Result.Party.Details.BankAccount.toDomain(): BankAccount =
    BankAccount(
        description = description,
        bankName = bankName,
        address = address.toDomain(),
        identifier = identifier.toDomain(),
        accountIdentification = accountIdentification.toDomain(),
        additionalAccountIdentifiers = additionalAccountIdentifiers.orEmpty()
            .map { it.toDomain() }
            .let { AccountIdentifiers(it) }
    )

fun Result.Party.Details.BankAccount.Identifier.toDomain(): AccountIdentifier =
    AccountIdentifier(
        id = id,
        scheme = scheme
    )

fun Result.Party.Details.BankAccount.AccountIdentification.toDomain(): AccountIdentifier =
    AccountIdentifier(
        id = id,
        scheme = scheme
    )

fun Result.Party.Details.LegalForm.toDomain(): LegalForm =
    LegalForm(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )