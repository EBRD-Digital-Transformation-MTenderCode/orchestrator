package com.procurement.orchestrator.infrastructure.client.web.evaluation.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.award.AwardId
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.document.DocumentType
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.measure.Amount
import com.procurement.orchestrator.domain.model.organization.OrganizationId
import com.procurement.orchestrator.domain.model.organization.datail.Scale
import com.procurement.orchestrator.domain.model.organization.datail.TypeOfSupplier
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctionId
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctionType
import com.procurement.orchestrator.domain.model.person.PersonId
import com.procurement.orchestrator.infrastructure.model.Version
import java.time.LocalDateTime

abstract class ValidateAwardDataAction : ProceduralAction<ValidateAwardDataAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "validateAwardData"

    class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender,
        @param:JsonProperty("awards") @field:JsonProperty("awards") val awards: List<Award>,
        @param:JsonProperty("operationType") @field:JsonProperty("operationType") val operationType: OperationTypeProcess,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("mdm") @field:JsonProperty("mdm") val mdm: Mdm?
    ) {
        data class Tender(
            @param:JsonProperty("lots") @field:JsonProperty("lots") val lots: List<Lot>
        ) {
            data class Lot(
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("value") @field:JsonProperty("value") val value: Value?,

                @param:JsonProperty("id") @field:JsonProperty("id") val id: LotId
            ) {
                data class Value(
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("amount") @field:JsonProperty("amount") val amount: Amount?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("currency") @field:JsonProperty("currency") val currency: String?
                )
            }
        }

        data class Award(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: AwardId,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("internalId") @field:JsonProperty("internalId") val internalId: String?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("value") @field:JsonProperty("value") val value: Value?,

            @param:JsonProperty("suppliers") @field:JsonProperty("suppliers") val suppliers: List<Supplier>,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>?
        ) {
            data class Value(
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("amount") @field:JsonProperty("amount") val amount: Amount?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("currency") @field:JsonProperty("currency") val currency: String?
            )

            data class Supplier(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: OrganizationId,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("name") @field:JsonProperty("name") val name: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier?,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("additionalIdentifiers") @field:JsonProperty("additionalIdentifiers") val additionalIdentifiers: List<AdditionalIdentifier>?,

                @param:JsonProperty("address") @field:JsonProperty("address") val address: Address,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("contactPoint") @field:JsonProperty("contactPoint") val contactPoint: ContactPoint?,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("persones") @field:JsonProperty("persones") val persones: List<Persone>?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("details") @field:JsonProperty("details") val details: Details?
            ) {
                data class Identifier(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("legalName") @field:JsonProperty("legalName") val legalName: String?,

                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                )

                data class AdditionalIdentifier(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("legalName") @field:JsonProperty("legalName") val legalName: String?,

                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                )

                data class Address(
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("streetAddress") @field:JsonProperty("streetAddress") val streetAddress: String?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("postalCode") @field:JsonProperty("postalCode") val postalCode: String?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("addressDetails") @field:JsonProperty("addressDetails") val addressDetails: AddressDetails?
                ) {
                    data class AddressDetails(
                        @param:JsonProperty("country") @field:JsonProperty("country") val country: Country,
                        @param:JsonProperty("region") @field:JsonProperty("region") val region: Region,
                        @param:JsonProperty("locality") @field:JsonProperty("locality") val locality: Locality
                    ) {
                        data class Country(
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String
                        )

                        data class Region(
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String
                        )

                        data class Locality(
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String
                        )
                    }
                }

                data class ContactPoint(
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("email") @field:JsonProperty("email") val email: String?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("telephone") @field:JsonProperty("telephone") val telephone: String?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("faxNumber") @field:JsonProperty("faxNumber") val faxNumber: String?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("url") @field:JsonProperty("url") val url: String?
                )

                data class Persone(
                    @param:JsonProperty("title") @field:JsonProperty("title") val title: String,
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: PersonId,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier?,

                    @param:JsonProperty("businessFunctions") @field:JsonProperty("businessFunctions") val businessFunctions: List<BusinessFunction>
                ) {
                    data class Identifier(
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                    )

                    data class BusinessFunction(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: BusinessFunctionId,

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
                            @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: DocumentType,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("title") @field:JsonProperty("title") val title: String?,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("description") @field:JsonProperty("description") val description: String?
                        )
                    }
                }

                data class Details(
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("typeOfSupplier") @field:JsonProperty("typeOfSupplier") val typeOfSupplier: TypeOfSupplier?,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @param:JsonProperty("mainEconomicActivities") @field:JsonProperty("mainEconomicActivities") val mainEconomicActivities: List<MainEconomicActivity>?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("scale") @field:JsonProperty("scale") val scale: Scale?,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @param:JsonProperty("permits") @field:JsonProperty("permits") val permits: List<Permit>?,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @param:JsonProperty("bankAccounts") @field:JsonProperty("bankAccounts") val bankAccounts: List<BankAccount>?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("legalForm") @field:JsonProperty("legalForm") val legalForm: LegalForm?
                ) {
                    data class MainEconomicActivity(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                    )

                    data class Permit(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("url") @field:JsonProperty("url") val url: String?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("permitDetails") @field:JsonProperty("permitDetails") val permitDetails: PermitDetails?
                    ) {
                        data class PermitDetails(
                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("issuedBy") @field:JsonProperty("issuedBy") val issuedBy: IssuedBy?,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("issuedThought") @field:JsonProperty("issuedThought") val issuedThought: IssuedThought?,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("validityPeriod") @field:JsonProperty("validityPeriod") val validityPeriod: ValidityPeriod?
                        ) {
                            data class IssuedBy(
                                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                                @JsonInclude(JsonInclude.Include.NON_NULL)
                                @param:JsonProperty("name") @field:JsonProperty("name") val name: String?
                            )

                            data class IssuedThought(
                                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                                @JsonInclude(JsonInclude.Include.NON_NULL)
                                @param:JsonProperty("name") @field:JsonProperty("name") val name: String?
                            )

                            data class ValidityPeriod(
                                @JsonInclude(JsonInclude.Include.NON_NULL)
                                @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime?,

                                @JsonInclude(JsonInclude.Include.NON_NULL)
                                @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime?
                            )
                        }
                    }

                    data class BankAccount(
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("bankName") @field:JsonProperty("bankName") val bankName: String?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("address") @field:JsonProperty("address") val address: Address?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("accountIdentification") @field:JsonProperty("accountIdentification") val accountIdentification: AccountIdentification?,

                        @JsonInclude(JsonInclude.Include.NON_EMPTY)
                        @param:JsonProperty("additionalAccountIdentifiers") @field:JsonProperty("additionalAccountIdentifiers") val additionalAccountIdentifiers: List<AdditionalAccountIdentifier>?
                    ) {
                        data class Address(
                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("streetAddress") @field:JsonProperty("streetAddress") val streetAddress: String?,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("postalCode") @field:JsonProperty("postalCode") val postalCode: String?,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("addressDetails") @field:JsonProperty("addressDetails") val addressDetails: AddressDetails?
                        ) {
                            data class AddressDetails(
                                @param:JsonProperty("country") @field:JsonProperty("country") val country: Country,
                                @param:JsonProperty("region") @field:JsonProperty("region") val region: Region,
                                @param:JsonProperty("locality") @field:JsonProperty("locality") val locality: Locality
                            ) {
                                data class Country(
                                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                                    @JsonInclude(JsonInclude.Include.NON_NULL)
                                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String
                                )

                                data class Region(
                                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                                    @JsonInclude(JsonInclude.Include.NON_NULL)
                                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String
                                )

                                data class Locality(
                                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                                    @JsonInclude(JsonInclude.Include.NON_NULL)
                                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String
                                )
                            }
                        }

                        data class Identifier(
                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                        )

                        data class AccountIdentification(
                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                        )

                        data class AdditionalAccountIdentifier(
                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                        )
                    }

                    data class LegalForm(
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                    )
                }
            }

            data class Document(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: DocumentId,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("title") @field:JsonProperty("title") val title: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: DocumentType
            )
        }

        data class Mdm(
            @param:JsonProperty("registrationSchemes") @field:JsonProperty("registrationSchemes") val registrationSchemes: List<RegistrationScheme>
        ) {
            data class RegistrationScheme(
                @param:JsonProperty("country") @field:JsonProperty("country") val country: String,
                @param:JsonProperty("schemes") @field:JsonProperty("schemes") val schemes: List<String>
            )
        }
    }
}
