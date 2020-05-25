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
import com.procurement.orchestrator.domain.model.candidate.Candidates
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.identifier.Identifier
import com.procurement.orchestrator.domain.model.identifier.Identifiers
import com.procurement.orchestrator.domain.model.organization.ContactPoint
import com.procurement.orchestrator.domain.model.organization.Organization
import com.procurement.orchestrator.domain.model.organization.OrganizationReference
import com.procurement.orchestrator.domain.model.organization.datail.MainEconomicActivities
import com.procurement.orchestrator.domain.model.organization.datail.MainEconomicActivity
import com.procurement.orchestrator.domain.model.organization.datail.account.AccountIdentifier
import com.procurement.orchestrator.domain.model.organization.datail.account.AccountIdentifiers
import com.procurement.orchestrator.domain.model.organization.datail.account.BankAccount
import com.procurement.orchestrator.domain.model.organization.datail.account.BankAccounts
import com.procurement.orchestrator.domain.model.organization.datail.legalform.LegalForm
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunction
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctions
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.person.Person
import com.procurement.orchestrator.domain.model.person.PersonId
import com.procurement.orchestrator.domain.model.person.Persons
import com.procurement.orchestrator.domain.model.requirement.RequirementReference
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponse
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponses
import com.procurement.orchestrator.domain.model.submission.Details
import com.procurement.orchestrator.domain.model.submission.Submission
import com.procurement.orchestrator.domain.model.submission.Submissions
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import org.springframework.stereotype.Component

@Component
class InitializeCreateSubmissionProcessDelegate(
    val logger: Logger,
    private val transform: Transform,
    operationStepRepository: OperationStepRepository,
    processInitializerRepository: ProcessInitializerRepository
) : AbstractInitializeProcessDelegate(logger, transform, operationStepRepository, processInitializerRepository) {

    override fun updateGlobalContext(
        camundaContext: CamundaContext,
        globalContext: CamundaGlobalContext
    ): MaybeFail<Fail.Incident> {
        val payload: CreateSubmission.Request.Payload =
            transform.tryDeserialization(camundaContext.request.payload, CreateSubmission.Request.Payload::class.java)
                .orReturnFail { return MaybeFail.fail(it) }

        globalContext.submissions = buildSubmissions(payload)

        return MaybeFail.none()
    }

    private fun buildSubmissions(payload: CreateSubmission.Request.Payload): Submissions = Submissions(
        details = Details(
            payload.submission
                .let { submission ->
                    Submission(
                        id = submission.id,
                        requirementResponses = submission.requirementResponses
                            .map { requirementResponse ->
                                RequirementResponse(
                                    id = requirementResponse.id,
                                    value = requirementResponse.value,
                                    requirement = RequirementReference(
                                        id = requirementResponse.requirement.id
                                    ),
                                    relatedCandidate = requirementResponse.relatedCandidate
                                        .let { relatedCandidate ->
                                            OrganizationReference(
                                                id = relatedCandidate.identifier
                                                    .let { identifier ->
                                                        identifier.scheme + "-" + identifier.id
                                                    },
                                                name = relatedCandidate.name
                                            )
                                        }
                                )
                            }
                            .let {
                                RequirementResponses(it)
                            },
                        candidates = submission.candidates
                            .map { candidate ->
                                Organization(
                                    id = candidate.identifier
                                        .let { identifier ->
                                            identifier.scheme + "-" + identifier.id
                                        },
                                    identifier = candidate.identifier
                                        .let { identifier ->
                                            Identifier(
                                                scheme = identifier.scheme,
                                                id = identifier.id,
                                                legalName = identifier.legalName,
                                                uri = identifier.uri
                                            )
                                        },
                                    additionalIdentifiers = candidate.additionalIdentifiers
                                        .map { identifier ->
                                            Identifier(
                                                scheme = identifier.scheme,
                                                id = identifier.id,
                                                legalName = identifier.legalName,
                                                uri = identifier.uri
                                            )
                                        }
                                        .let { Identifiers(it) },
                                    address = candidate.address
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
                                                                        description = country.description,
                                                                        uri = country.uri
                                                                    )
                                                                },
                                                            region = addressDetails.region
                                                                .let { region ->
                                                                    RegionDetails(
                                                                        scheme = region.scheme,
                                                                        id = region.id,
                                                                        description = region.description,
                                                                        uri = region.uri
                                                                    )
                                                                },
                                                            locality = addressDetails.locality
                                                                .let { locality ->
                                                                    LocalityDetails(
                                                                        scheme = locality.scheme,
                                                                        id = locality.id,
                                                                        description = locality.description,
                                                                        uri = locality.uri
                                                                    )
                                                                }
                                                        )
                                                    }
                                            )
                                        },
                                    contactPoint = candidate.contactPoint
                                        .let { contactPoint ->
                                            ContactPoint(
                                                name = contactPoint.name,
                                                email = contactPoint.email,
                                                telephone = contactPoint.telephone,
                                                faxNumber = contactPoint.faxNumber,
                                                url = contactPoint.url
                                            )
                                        },
                                    persons = candidate.persons
                                        .map { person ->
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
                                                                .map { document ->
                                                                    Document(
                                                                        documentType = document.documentType,
                                                                        id = document.id,
                                                                        title = document.title,
                                                                        description = document.description
                                                                    )
                                                                }
                                                                .let { Documents(it) }
                                                        )
                                                    }
                                                    .let { BusinessFunctions(it) }
                                            )
                                        }
                                        .let { Persons(it) },
                                    details = candidate.details
                                        .let { details ->
                                            com.procurement.orchestrator.domain.model.organization.datail.Details(
                                                typeOfSupplier = details.typeOfSupplier,
                                                mainEconomicActivities = details.mainEconomicActivities
                                                    .map { mainEconomicActivity ->
                                                        MainEconomicActivity(
                                                            scheme = mainEconomicActivity.scheme,
                                                            id = mainEconomicActivity.id,
                                                            description = mainEconomicActivity.description,
                                                            uri = mainEconomicActivity.uri
                                                        )
                                                    }
                                                    .let { MainEconomicActivities(it) },
                                                bankAccounts = details.bankAccounts
                                                    .map { bankAccount ->
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
                                                                                                description = country.description,
                                                                                                uri = country.uri
                                                                                            )
                                                                                        },
                                                                                    region = addressDetails.region
                                                                                        .let { region ->
                                                                                            RegionDetails(
                                                                                                scheme = region.scheme,
                                                                                                id = region.id,
                                                                                                description = region.description,
                                                                                                uri = region.uri
                                                                                            )
                                                                                        },
                                                                                    locality = addressDetails.locality
                                                                                        .let { locality ->
                                                                                            LocalityDetails(
                                                                                                scheme = locality.scheme,
                                                                                                id = locality.id,
                                                                                                description = locality.description,
                                                                                                uri = locality.uri
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
                                                                .map { accountIdentifier ->
                                                                    AccountIdentifier(
                                                                        scheme = accountIdentifier.scheme,
                                                                        id = accountIdentifier.id
                                                                    )
                                                                }
                                                                .let { AccountIdentifiers(it) }
                                                        )
                                                    }
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
                                                scale = details.scale
                                            )
                                        }
                                )
                            }
                            .let { Candidates(it) },
                        documents = submission.documents
                            .map { document ->
                                Document(
                                    documentType = document.documentType,
                                    id = document.id,
                                    title = document.title,
                                    description = document.description
                                )
                            }
                            .let { Documents(it) }
                    )
                }
        )
    )
}
