package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getConfirmationResponseIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getContractIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getPartyIfOnlyOne
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
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
import com.procurement.orchestrator.domain.model.organization.datail.Details
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunction
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctions
import com.procurement.orchestrator.domain.model.party.Parties
import com.procurement.orchestrator.domain.model.party.Party
import com.procurement.orchestrator.domain.model.party.PartyRole
import com.procurement.orchestrator.domain.model.party.PartyRoles
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.person.Person
import com.procurement.orchestrator.domain.model.person.Persons
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.AccessCommands
import com.procurement.orchestrator.infrastructure.client.web.access.action.PersonesProcessingAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class AccessPersonesProcessingDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<AccessPersonesProcessingDelegate.Parameters, PersonesProcessingAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {
    companion object {
        private const val PARAMETER_NAME_ROLE = "role"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val parameter = parameterContainer.getString(PARAMETER_NAME_ROLE)
            .orForwardFail { fail -> return fail }

        val role = PartyRole.orNull(parameter)
            ?: return Result.failure(
                Fail.Incident.Bpmn.Parameter.UnknownValue(
                    name = PARAMETER_NAME_ROLE,
                    actualValue = parameter,
                    expectedValues = PartyRole.allowedElements.keysAsStrings()
                )
            )

        return success(Parameters(role = role))
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<PersonesProcessingAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val person = context.getContractIfOnlyOne()
            .orForwardFail { return it }
            .getConfirmationResponseIfOnlyOne()
            .orForwardFail { return it }
            .relatedPerson!!

        val party = context.getPartyIfOnlyOne()
            .orForwardFail { return it }

        return accessClient.personesProcessing(
            id = commandId,
            params = PersonesProcessingAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                role = parameters.role,
                parties = listOf(
                    PersonesProcessingAction.Params.Party(
                        id = party.id,
                        persones = PersonesProcessingAction.Params.Party.Persone(
                            id = person.id,
                            title = person.title,
                            name = person.name,
                            identifier = person.identifier?.let { identifier ->
                                PersonesProcessingAction.Params.Party.Persone.Identifier(
                                    id = identifier.id,
                                    uri = identifier.uri,
                                    scheme = identifier.scheme
                                )
                            },
                            businessFunctions = person.businessFunctions.map { businessFunction ->
                                PersonesProcessingAction.Params.Party.Persone.BusinessFunction(
                                    id = businessFunction.id,
                                    jobTitle = businessFunction.jobTitle,
                                    type = businessFunction.type,
                                    period = businessFunction.period?.let { period ->
                                        PersonesProcessingAction.Params.Party.Persone.BusinessFunction.Period(period.startDate)
                                    },
                                    documents = businessFunction.documents.map { document ->
                                        PersonesProcessingAction.Params.Party.Persone.BusinessFunction.Document(
                                            id = document.id,
                                            title = document.title,
                                            documentType = document.documentType,
                                            description = document.description
                                        )
                                    }
                                )
                            }
                        ).let { listOf(it) }
                    ))
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        result: Option<PersonesProcessingAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.ACCESS,
                    action = AccessCommands.PersonesProcessing
                )
            )

        val receivedParties = data.parties
            .map { party -> party.toDomain() }
            .let { Parties(it) }

        context.parties = context.parties updateBy receivedParties

        return MaybeFail.none()
    }

    private fun PersonesProcessingAction.Result.Party.toDomain(): Party =
        Party(
            id = id,
            name = name,
            identifier = Identifier(
                id = identifier.id,
                uri = identifier.uri,
                legalName = identifier.legalName,
                scheme = identifier.scheme
            ),
            additionalIdentifiers = additionalIdentifiers
                ?.map { additionalIdentifier ->
                    Identifier(
                        id = additionalIdentifier.id,
                        scheme = additionalIdentifier.scheme,
                        legalName = additionalIdentifier.legalName,
                        uri = additionalIdentifier.uri
                    )

                }.orEmpty()
                .let { Identifiers(it) },
            address = address
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
                                                id = country.id,
                                                description = country.description,
                                                scheme = country.scheme,
                                                uri = country.uri
                                            )
                                        },
                                    region = addressDetails.region
                                        .let { region ->
                                            RegionDetails(
                                                id = region.id,
                                                description = region.description,
                                                scheme = region.scheme,
                                                uri = region.uri
                                            )
                                        },
                                    locality = addressDetails.locality
                                        .let { locality ->
                                            LocalityDetails(
                                                id = locality.id,
                                                description = locality.description,
                                                scheme = locality.scheme,
                                                uri = locality.uri
                                            )
                                        }
                                )
                            }
                    )
                },
            contactPoint = contactPoint
                .let { contactPoint ->
                    ContactPoint(
                        name = contactPoint.name,
                        email = contactPoint.email,
                        telephone = contactPoint.telephone,
                        faxNumber = contactPoint.faxNumber,
                        url = contactPoint.url
                    )
                },
            persons = persones
                .map { person ->
                    Person(
                        id = person.id,
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
                                        }
                                        .orEmpty()
                                        .let { Documents(it) }
                                )
                            }
                            .let { BusinessFunctions(it) }
                    )
                }
                .let { Persons(it) },
            details = details?.let { details ->
                Details(
                    typeOfBuyer = details.typeOfBuyer,
                    mainSectoralActivity = details.mainSectoralActivity,
                    mainGeneralActivity = details.mainGeneralActivity
                )
            },
            roles = PartyRoles(roles)

        )

    data class Parameters(val role: PartyRole)
}
