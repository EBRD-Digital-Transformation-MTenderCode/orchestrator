package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
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
import com.procurement.orchestrator.domain.model.document.RelatedConfirmations
import com.procurement.orchestrator.domain.model.identifier.Identifier
import com.procurement.orchestrator.domain.model.identifier.Identifiers
import com.procurement.orchestrator.domain.model.lot.RelatedLots
import com.procurement.orchestrator.domain.model.organization.ContactPoint
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
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetOrganizationsAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class AccessGetOrganizationsDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<AccessGetOrganizationsDelegate.Parameters, GetOrganizationsAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {
    companion object {
        private const val PARAMETER_NAME_ROLE = "role"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val role: PartyRole = parameterContainer.getString(PARAMETER_NAME_ROLE)
            .orForwardFail { fail -> return fail }
            .let { role ->
                PartyRole.orNull(role)
                    ?: return Result.failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = PARAMETER_NAME_ROLE,
                            actualValue = role,
                            expectedValues = PartyRole.allowedElements.keysAsStrings()
                        )
                    )
            }
        return success(Parameters(role = role))
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<GetOrganizationsAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        return accessClient.getOrganizations(
            id = commandId,
            params = GetOrganizationsAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                role = parameters.role
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        result: Option<GetOrganizationsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(service = ExternalServiceName.ACCESS, action = AccessCommands.GetOrganizations)
            )

        val receivedParties = data.parties.map { buildParty(it) }
        context.parties = Parties(receivedParties)

        return MaybeFail.none()
    }

    class Parameters(val role: PartyRole)

    private fun buildParty(party: GetOrganizationsAction.Result.Party) = Party(
        id = party.id,
        name = party.name,
        roles = PartyRoles(),
        details = null,
        address = party.address
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
        identifier = party.identifier
            .let { identifier ->
                Identifier(
                    id = identifier.id,
                    legalName = identifier.legalName,
                    scheme = identifier.scheme,
                    uri = identifier.uri
                )
            },
        additionalIdentifiers = Identifiers(
            party.additionalIdentifiers
                ?.map { additionalIdentifier ->
                    Identifier(
                        id = additionalIdentifier.id,
                        legalName = additionalIdentifier.legalName,
                        scheme = additionalIdentifier.scheme,
                        uri = additionalIdentifier.uri
                    )
                }
                .orEmpty()
        ),
        contactPoint = party.contactPoint
            .let { contactPoint ->
                ContactPoint(
                    name = contactPoint.name,
                    telephone = contactPoint.telephone,
                    faxNumber = contactPoint.faxNumber,
                    email = contactPoint.email,
                    url = contactPoint.url
                )
            },
        persons = Persons(
            party.persons
                ?.map { person ->
                    Person(
                        id = person.id,
                        title = person.title,
                        name = person.name,
                        identifier = person.identifier
                            .let { identifier ->
                                Identifier(
                                    id = identifier.id,
                                    legalName = null,
                                    scheme = identifier.scheme,
                                    uri = identifier.uri
                                )
                            },
                        businessFunctions = BusinessFunctions(
                            person.businessFunctions
                                .map { businessFunction ->
                                    BusinessFunction(
                                        id = businessFunction.id,
                                        type = businessFunction.type,
                                        jobTitle = businessFunction.jobTitle,
                                        period = businessFunction.period
                                            .let { period ->
                                                Period(
                                                    startDate = period.startDate,
                                                    endDate = null,
                                                    durationInDays = null,
                                                    maxExtentDate = null
                                                )
                                            },
                                        documents = Documents(
                                            businessFunction.documents
                                                ?.map { document ->
                                                    Document(
                                                        id = document.id,
                                                        title = document.title,
                                                        url = null,
                                                        description = document.description,
                                                        documentType = document.documentType,
                                                        relatedLots = RelatedLots(),
                                                        datePublished = null,
                                                        dateModified = null,
                                                        relatedConfirmations = RelatedConfirmations(),
                                                        format = null,
                                                        language = null
                                                    )
                                                }
                                                .orEmpty()
                                        )
                                    )
                                }
                        )
                    )
                }
                .orEmpty()
        )
    )
}
