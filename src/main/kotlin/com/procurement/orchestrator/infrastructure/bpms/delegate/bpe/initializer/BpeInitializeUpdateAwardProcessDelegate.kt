package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer

import com.procurement.orchestrator.application.model.context.CamundaContext
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetId
import com.procurement.orchestrator.application.repository.ProcessInitializerRepository
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.model.address.Address
import com.procurement.orchestrator.domain.model.address.AddressDetails
import com.procurement.orchestrator.domain.model.address.country.CountryDetails
import com.procurement.orchestrator.domain.model.address.locality.LocalityDetails
import com.procurement.orchestrator.domain.model.address.region.RegionDetails
import com.procurement.orchestrator.domain.model.award.Award
import com.procurement.orchestrator.domain.model.award.AwardId
import com.procurement.orchestrator.domain.model.award.Awards
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.identifier.Identifier
import com.procurement.orchestrator.domain.model.identifier.Identifiers
import com.procurement.orchestrator.domain.model.organization.ContactPoint
import com.procurement.orchestrator.domain.model.organization.Organization
import com.procurement.orchestrator.domain.model.organization.Organizations
import com.procurement.orchestrator.domain.model.organization.datail.Details
import com.procurement.orchestrator.domain.model.organization.datail.MainEconomicActivities
import com.procurement.orchestrator.domain.model.organization.datail.MainEconomicActivity
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
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctions
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.person.Person
import com.procurement.orchestrator.domain.model.person.PersonId
import com.procurement.orchestrator.domain.model.person.Persons
import com.procurement.orchestrator.domain.model.value.Value
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import org.springframework.stereotype.Component

@Component
class BpeInitializeUpdateAwardProcessDelegate(
    val logger: Logger,
    transform: Transform,
    operationStepRepository: OperationStepRepository,
    processInitializerRepository: ProcessInitializerRepository
) : AbstractInitializeProcessDelegate(logger, transform, operationStepRepository, processInitializerRepository) {

    override fun updateGlobalContext(
        camundaContext: CamundaContext,
        globalContext: CamundaGlobalContext
    ): MaybeFail<Fail> {

        val payload: UpdateAwardProcess.Request.Payload =
            parsePayload(camundaContext.request.payload, UpdateAwardProcess.Request.Payload::class.java)
                .orReturnFail { return MaybeFail.fail(it) }

        val requestId = camundaContext.request.tryGetId()
            .orReturnFail { return MaybeFail.fail(it) }
        val awardId = AwardId.tryCreateOrNull(requestId)!!

        globalContext.awards = buildAwards(payload, awardId)

        return MaybeFail.none()
    }

    private fun buildAwards(
        payload: UpdateAwardProcess.Request.Payload,
        awardId: AwardId
    ): Awards = payload.award.let { award ->
        Award(
            id = awardId,
            internalId = award.internalId,
            description = award.description,
            value = award.value
                ?.let { value ->
                    Value(
                        amount = value.amount
                    )
                },
            suppliers = award.suppliers.map { supplier ->
                Organization(
                    id = supplier.identifier
                        .let { identifier ->
                            identifier.scheme + "-" + identifier.id
                        },
                    name = supplier.name,
                    identifier = supplier.identifier
                        .let { identifier ->
                            Identifier(
                                scheme = identifier.scheme,
                                id = identifier.id,
                                legalName = identifier.legalName,
                                uri = identifier.uri
                            )
                        },
                    additionalIdentifiers = supplier.additionalIdentifiers
                        ?.map { identifier ->
                            Identifier(
                                scheme = identifier.scheme,
                                id = identifier.id,
                                legalName = identifier.legalName,
                                uri = identifier.uri
                            )
                        }.orEmpty()
                        .let { Identifiers(it) },
                    address = supplier.address
                        .let { address ->
                            Address(
                                streetAddress = address.streetAddress,
                                postalCode = address.postalCode,
                                addressDetails = address.addressDetails
                                    .let { addressDetails ->
                                        AddressDetails(
                                            country = addressDetails.country
                                                .let { country ->
                                                    CountryDetails(
                                                        scheme = country.scheme,
                                                        id = country.id,
                                                        description = country.description
                                                    )
                                                },
                                            region = addressDetails.region
                                                .let { region ->
                                                    RegionDetails(
                                                        scheme = region.scheme,
                                                        id = region.id,
                                                        description = region.description
                                                    )
                                                },
                                            locality = addressDetails.locality
                                                .let { locality ->
                                                    LocalityDetails(
                                                        scheme = locality.scheme,
                                                        id = locality.id,
                                                        description = locality.description
                                                    )
                                                }
                                        )
                                    }
                            )
                        },
                    contactPoint = supplier.contactPoint
                        .let { contactPoint ->
                            ContactPoint(
                                name = contactPoint.name,
                                email = contactPoint.email,
                                telephone = contactPoint.telephone,
                                faxNumber = contactPoint.faxNumber,
                                url = contactPoint.url
                            )
                        },
                    persons = supplier.persones
                        ?.map { person ->
                            Person(
                                id = PersonId.generate(
                                    scheme = person.identifier.scheme,
                                    id = person.identifier.id
                                ),
                                identifier = person.identifier
                                    .let { identifier ->
                                        Identifier(
                                            scheme = identifier.scheme,
                                            id = identifier.id,
                                            uri = identifier.uri
                                        )
                                    },
                                name = person.name,
                                title = person.title,
                                businessFunctions = person.businessFunctions
                                    .map { businessFunction ->
                                        BusinessFunction(
                                            id = businessFunction.id,
                                            type = businessFunction.type,
                                            jobTitle = businessFunction.jobTitle,
                                            period = Period(startDate = businessFunction.period.startDate),
                                            documents = businessFunction.documents
                                                ?.map { document ->
                                                    Document(
                                                        documentType = document.documentType,
                                                        id = document.id,
                                                        title = document.title,
                                                        description = document.description
                                                    )
                                                }.orEmpty()
                                                .let { Documents(it) }
                                        )
                                    }
                                    .let { BusinessFunctions(it) }
                            )
                        }.orEmpty()
                        .let { Persons(it) },
                    details = supplier.details
                        .let { details ->
                            Details(
                                typeOfSupplier = details.typeOfSupplier,
                                mainEconomicActivities = details.mainEconomicActivities
                                    ?.map { mainEconomicActivity ->
                                        MainEconomicActivity(
                                            scheme = mainEconomicActivity.scheme,
                                            id = mainEconomicActivity.id,
                                            description = mainEconomicActivity.description,
                                            uri = mainEconomicActivity.uri
                                        )
                                    }.orEmpty()
                                    .let { MainEconomicActivities(it) },
                                bankAccounts = details.bankAccounts
                                    ?.map { bankAccount ->
                                        BankAccount(
                                            description = bankAccount.description,
                                            bankName = bankAccount.bankName,
                                            address = bankAccount.address
                                                .let { address ->
                                                    Address(
                                                        streetAddress = address.streetAddress,
                                                        postalCode = address.postalCode,
                                                        addressDetails = address.addressDetails
                                                            .let { addressDetails ->
                                                                AddressDetails(
                                                                    country = addressDetails.country
                                                                        .let { country ->
                                                                            CountryDetails(
                                                                                scheme = country.scheme,
                                                                                id = country.id,
                                                                                description = country.description
                                                                            )
                                                                        },
                                                                    region = addressDetails.region
                                                                        .let { region ->
                                                                            RegionDetails(
                                                                                scheme = region.scheme,
                                                                                id = region.id,
                                                                                description = region.description
                                                                            )
                                                                        },
                                                                    locality = addressDetails.locality
                                                                        .let { locality ->
                                                                            LocalityDetails(
                                                                                scheme = locality.scheme,
                                                                                id = locality.id,
                                                                                description = locality.description
                                                                            )
                                                                        }
                                                                )
                                                            }
                                                    )
                                                },
                                            identifier = bankAccount.identifier
                                                .let { accountIdentifier ->
                                                    AccountIdentifier(
                                                        scheme = accountIdentifier.scheme,
                                                        id = accountIdentifier.id
                                                    )
                                                },
                                            accountIdentification = bankAccount.accountIdentification
                                                .let { accountIdentifier ->
                                                    AccountIdentifier(
                                                        scheme = accountIdentifier.scheme,
                                                        id = accountIdentifier.id
                                                    )

                                                },
                                            additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers
                                                ?.map { accountIdentifier ->
                                                    AccountIdentifier(
                                                        scheme = accountIdentifier.scheme,
                                                        id = accountIdentifier.id
                                                    )
                                                }.orEmpty()
                                                .let { AccountIdentifiers(it) }
                                        )
                                    }.orEmpty()
                                    .let { BankAccounts(it) },
                                legalForm = details.legalForm
                                    ?.let { legalForm ->
                                        LegalForm(
                                            scheme = legalForm.scheme,
                                            id = legalForm.id,
                                            description = legalForm.description,
                                            uri = legalForm.uri
                                        )
                                    },
                                scale = details.scale,
                                permits = details.permits
                                    ?.map { permit ->
                                        Permit(
                                            scheme = permit.scheme,
                                            id = permit.id,
                                            url = permit.url,
                                            permitDetails = permit.permitDetails
                                                .let { permitDetails ->
                                                    PermitDetails(
                                                        issuedBy = permitDetails.issuedBy
                                                            .let { issuedBy ->
                                                                Issue(
                                                                    id = issuedBy.id,
                                                                    name = issuedBy.name
                                                                )
                                                            },
                                                        issuedThought = permitDetails.issuedThought
                                                            .let { issuedThought ->
                                                                Issue(
                                                                    id = issuedThought.id,
                                                                    name = issuedThought.name
                                                                )
                                                            },
                                                        validityPeriod = permitDetails.validityPeriod
                                                            .let { validityPeriod ->
                                                                Period(
                                                                    startDate = validityPeriod.startDate,
                                                                    endDate = validityPeriod.startDate
                                                                )
                                                            }
                                                    )
                                                }
                                        )
                                    }.orEmpty()
                                    .let { Permits(it) }

                            )
                        }
                )
            }.let { Organizations(it) },
            documents = payload.award.documents
                ?.map { document ->
                    Document(
                        documentType = document.documentType,
                        id = document.id,
                        title = document.title,
                        description = document.description
                    )
                }.orEmpty()
                .let { Documents(it) }
        )
    }
        .let { Awards(listOf(it)) }
}
