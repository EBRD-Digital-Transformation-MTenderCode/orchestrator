package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer

import com.procurement.orchestrator.application.model.context.CamundaContext
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
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
import com.procurement.orchestrator.domain.model.bid.Bid
import com.procurement.orchestrator.domain.model.bid.BidId
import com.procurement.orchestrator.domain.model.bid.Bids
import com.procurement.orchestrator.domain.model.bid.BidsDetails
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.document.DocumentReference
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.identifier.Identifier
import com.procurement.orchestrator.domain.model.identifier.Identifiers
import com.procurement.orchestrator.domain.model.item.Item
import com.procurement.orchestrator.domain.model.item.Items
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.lot.RelatedLots
import com.procurement.orchestrator.domain.model.organization.ContactPoint
import com.procurement.orchestrator.domain.model.organization.Organization
import com.procurement.orchestrator.domain.model.organization.OrganizationReference
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
import com.procurement.orchestrator.domain.model.requirement.RequirementReference
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponse
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponses
import com.procurement.orchestrator.domain.model.requirement.response.evidence.Evidence
import com.procurement.orchestrator.domain.model.requirement.response.evidence.Evidences
import com.procurement.orchestrator.domain.model.unit.Unit
import com.procurement.orchestrator.domain.model.value.Value
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import org.springframework.stereotype.Component

@Component
class BpeInitializeSubmitBidProcessDelegate(
    val logger: Logger,
    transform: Transform,
    operationStepRepository: OperationStepRepository,
    processInitializerRepository: ProcessInitializerRepository
) : AbstractInitializeProcessDelegate(logger, transform, operationStepRepository, processInitializerRepository) {

    override fun updateGlobalContext(
        camundaContext: CamundaContext,
        globalContext: CamundaGlobalContext
    ): MaybeFail<Fail> {

        val payload: SubmitBid.Request.Payload =
            parsePayload(camundaContext.request.payload, SubmitBid.Request.Payload::class.java)
                .orReturnFail { return MaybeFail.fail(it) }

        globalContext.bids = buildBids(BidId.randomUUID(), payload)

        return MaybeFail.none()
    }

    private fun buildBids(
        bidId: BidId,
        payload: SubmitBid.Request.Payload
    ): Bids = payload.bid
        .let { bid ->
            Bid(
                id = bidId,
                value = bid.value
                    .let { value ->
                        Value(
                            amount = value.amount,
                            currency = value.currency
                        )
                    },
                requirementResponses = bid.requirementResponses
                    ?.map { requirementResponse ->
                        RequirementResponse(
                            id = requirementResponse.id,
                            value = requirementResponse.value,
                            requirement = RequirementReference(requirementResponse.requirement.id),
                            relatedTenderer = requirementResponse.relatedTenderer
                                ?.let { relatedTenderer ->
                                    OrganizationReference(
                                        name = relatedTenderer.name,
                                        id = "${relatedTenderer.identifier.scheme}-${relatedTenderer.identifier.id}"
                                    )
                                },
                            evidences = Evidences(
                                requirementResponse.evidences
                                    ?.map { evidence ->
                                        Evidence(
                                            id = evidence.id,
                                            title = evidence.title,
                                            description = evidence.description,
                                            relatedDocument = evidence.relatedDocument?.let { DocumentReference(it.id) }
                                        )
                                    }
                                    .orEmpty()
                            ),
                            period = requirementResponse.period
                                ?.let { period ->
                                    Period(
                                        startDate = period.startDate,
                                        endDate = period.endDate
                                    )
                                }
                        )
                    }.orEmpty()
                    .let { RequirementResponses(it) },
                tenderers = bid.tenderers
                    .map { tenderer ->
                        Organization(
                            id = "${tenderer.identifier.scheme}-${tenderer.identifier.id}",
                            name = tenderer.name,
                            identifier = tenderer.identifier
                                .let { identifier ->
                                    Identifier(
                                        id = identifier.id,
                                        legalName = identifier.legalName,
                                        scheme = identifier.scheme,
                                        uri = identifier.uri
                                    )
                                },
                            additionalIdentifiers = tenderer.additionalIdentifiers
                                ?.map { additionalIdentifier ->
                                    Identifier(
                                        id = additionalIdentifier.id,
                                        legalName = additionalIdentifier.legalName,
                                        scheme = additionalIdentifier.scheme,
                                        uri = additionalIdentifier.uri
                                    )
                                }.orEmpty()
                                .let { Identifiers(it) },
                            address = tenderer.address
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
                            contactPoint = tenderer.contactPoint
                                .let { contactPoint ->
                                    ContactPoint(
                                        name = contactPoint.name,
                                        email = contactPoint.email,
                                        telephone = contactPoint.telephone,
                                        faxNumber = contactPoint.faxNumber,
                                        url = contactPoint.url
                                    )
                                },
                            persons = tenderer.persones
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
                                            }.let { BusinessFunctions(it) }
                                    )
                                }.orEmpty()
                                .let { Persons(it) },
                            details = tenderer.details
                                .let { detail ->
                                    Details(
                                        typeOfSupplier = detail.typeOfSupplier,
                                        mainEconomicActivities = detail.mainEconomicActivities
                                            ?.map { mainEconomicActivity ->
                                                MainEconomicActivity(
                                                    id = mainEconomicActivity.id,
                                                    scheme = mainEconomicActivity.scheme,
                                                    uri = mainEconomicActivity.uri,
                                                    description = mainEconomicActivity.description
                                                )
                                            }.orEmpty()
                                            .let { MainEconomicActivities(it) },
                                        scale = detail.scale,
                                        permits = detail.permits?.map { permit ->
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
                                        }
                                            .orEmpty()
                                            .let { Permits(it) },
                                        bankAccounts = detail.bankAccounts
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
                                        legalForm = detail.legalForm
                                            ?.let { legalForm ->
                                                LegalForm(
                                                    scheme = legalForm.scheme,
                                                    id = legalForm.id,
                                                    description = legalForm.description,
                                                    uri = legalForm.uri
                                                )
                                            }
                                    )
                                }
                        )
                    }.let { Organizations(it) },
                relatedLots = RelatedLots(bid.relatedLots),
                documents = bid.documents
                    ?.map { document ->
                        Document(
                            documentType = document.documentType,
                            id = document.id,
                            title = document.title,
                            description = document.description,
                            relatedLots = document.relatedLots.orEmpty()
                                .map { LotId.create(it) }
                                .let { RelatedLots(it) }
                        )
                    }
                    .orEmpty()
                    .let { Documents(it) },
                items = bid.items
                    ?.map { item ->
                        Item(
                            id = item.id,
                            unit = item.unit.let { unit ->
                                Unit(
                                    id = unit.id,
                                    value = unit.value
                                        .let { value ->
                                            Value(
                                                amount = value.amount,
                                                currency = value.currency
                                            )
                                        }
                                )
                            }
                        )
                    }.orEmpty()
                    .let { Items(it) }
            )
        }.let { Bids(details = BidsDetails(listOf(it))) }
}
