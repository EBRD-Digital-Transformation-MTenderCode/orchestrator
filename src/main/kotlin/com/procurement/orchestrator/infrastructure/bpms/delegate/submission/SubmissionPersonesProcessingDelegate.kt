package com.procurement.orchestrator.infrastructure.bpms.delegate.submission

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.SubmissionClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getConfirmationResponseIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getContractIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getPartyIfOnlyOne
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.address.Address
import com.procurement.orchestrator.domain.model.address.AddressDetails
import com.procurement.orchestrator.domain.model.address.country.CountryDetails
import com.procurement.orchestrator.domain.model.address.locality.LocalityDetails
import com.procurement.orchestrator.domain.model.address.region.RegionDetails
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.identifier.Identifier
import com.procurement.orchestrator.domain.model.identifier.Identifiers
import com.procurement.orchestrator.domain.model.organization.ContactPoint
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunction
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctions
import com.procurement.orchestrator.domain.model.party.Parties
import com.procurement.orchestrator.domain.model.party.Party
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.person.Person
import com.procurement.orchestrator.domain.model.person.Persons
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.submission.SubmissionCommands
import com.procurement.orchestrator.infrastructure.client.web.submission.action.PersonesProcessingAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class SubmissionPersonesProcessingDelegate(
    logger: Logger,
    private val submissionClient: SubmissionClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, PersonesProcessingAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {
    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        Unit.asSuccess()

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<PersonesProcessingAction.Result>, Fail.Incident> {
        val processInfo = context.processInfo

        val party = context.getPartyIfOnlyOne()
            .orForwardFail { return it }

        val person = context.getContractIfOnlyOne()
            .orForwardFail { return it }
            .getConfirmationResponseIfOnlyOne()
            .orForwardFail { return it }
            .relatedPerson!!

        return submissionClient.personesProcessing(
            id = commandId,
            params = PersonesProcessingAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                parties = PersonesProcessingAction.Params.Party(
                    id = party.id,
                    persones = PersonesProcessingAction.Params.Party.Persone(
                        id = person.id,
                        title = person.title,
                        name = person.name,
                        identifier = person.identifier?.let { identifier ->
                            PersonesProcessingAction.Params.Party.Persone.Identifier(
                                scheme = identifier.scheme,
                                id = identifier.id,
                                uri = identifier.uri
                            )
                        },
                        businessFunctions = person.businessFunctions
                            .map { businessFunction ->
                                PersonesProcessingAction.Params.Party.Persone.BusinessFunction(
                                    id = businessFunction.id,
                                    type = businessFunction.type,
                                    jobTitle = businessFunction.jobTitle,
                                    period = businessFunction.period
                                        ?.let { period ->
                                            PersonesProcessingAction.Params.Party.Persone.BusinessFunction.Period(
                                                startDate = period.startDate
                                            )
                                        },
                                    documents = businessFunction.documents
                                        .map { document ->
                                            PersonesProcessingAction.Params.Party.Persone.BusinessFunction.Document(
                                                id = document.id,
                                                documentType = document.documentType,
                                                title = document.title,
                                                description = document.description
                                            )
                                        }
                                )
                            }

                    ).let { listOf(it) }
                ).let { listOf(it) }

            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<PersonesProcessingAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.SUBMISSION,
                    action = SubmissionCommands.PersonesProcessing
                )
            )

        val receivedParties = data.parties
            .map { party ->
                Party(
                    id = party.id,
                    name = party.name,
                    identifier = party.identifier
                        .let { identifier ->
                            Identifier(
                                id = identifier.id,
                                scheme = identifier.scheme,
                                legalName = identifier.legalName,
                                uri = identifier.uri
                            )
                        },
                    additionalIdentifiers = party.additionalIdentifiers
                        ?.map { additionalIdentifier ->
                            Identifier(
                                id = additionalIdentifier.id,
                                scheme = additionalIdentifier.id,
                                legalName = additionalIdentifier.legalName,
                                uri = additionalIdentifier.uri
                            )
                        }.orEmpty()
                        .let { Identifiers(it) },
                    address = party.address
                        .let { adress ->
                            Address(
                                streetAddress = adress.streetAddress,
                                postalCode = adress.postalCode,
                                addressDetails = adress.addressDetails
                                    .let { adressDetails ->
                                        AddressDetails(
                                            country = adressDetails.country
                                                .let { country ->
                                                    CountryDetails(
                                                        scheme = country.scheme,
                                                        id = country.id,
                                                        description = country.description,
                                                        uri = country.uri
                                                    )
                                                },
                                            region = adressDetails.region
                                                .let { region ->
                                                    RegionDetails(
                                                        scheme = region.scheme,
                                                        id = region.id,
                                                        description = region.description,
                                                        uri = region.uri
                                                    )
                                                },
                                            locality = adressDetails.locality
                                                .let { locality ->
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
                        },
                    contactPoint = party.contactPoint
                        .let { contactPoint ->
                            ContactPoint(
                                name = contactPoint.name,
                                email = contactPoint.email,
                                telephone = contactPoint.telephone,
                                faxNumber = contactPoint.faxNumber,
                                url = contactPoint.url
                            )
                        },
                    persons = party.persones
                        .map { persone ->
                            Person(
                                id = persone.id,
                                identifier = persone.identifier
                                    .let { identifier ->
                                        Identifier(
                                            scheme = identifier.scheme,
                                            id = identifier.id,
                                            uri = identifier.uri
                                        )
                                    },
                                name = persone.name,
                                title = persone.title,
                                businessFunctions = persone.businessFunctions
                                    .map { businessFunction ->
                                        BusinessFunction(
                                            id = businessFunction.id,
                                            type = businessFunction.type,
                                            jobTitle = businessFunction.jobTitle,
                                            period = Period(
                                                startDate = businessFunction.period.startDate
                                            ),
                                            documents = businessFunction.documents
                                                ?.map { document ->
                                                    Document(
                                                        id = document.id,
                                                        documentType = document.documentType,
                                                        title = document.title,
                                                        description = document.description
                                                    )
                                                }.orEmpty()
                                                .let { Documents(it) }
                                        )
                                    }.let { BusinessFunctions(it) }

                            )
                        }.let { Persons(it) }
                )
            }
            .let { Parties(it) }

        context.parties = context.parties updateBy receivedParties

        return MaybeFail.none()
    }
}