package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.members.Parties
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.address.Address
import com.procurement.orchestrator.domain.model.address.AddressDetails
import com.procurement.orchestrator.domain.model.address.country.CountryDetails
import com.procurement.orchestrator.domain.model.address.locality.LocalityDetails
import com.procurement.orchestrator.domain.model.address.region.RegionDetails
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.identifier.Identifier
import com.procurement.orchestrator.domain.model.organization.ContactPoint
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunction
import com.procurement.orchestrator.domain.model.party.Party
import com.procurement.orchestrator.domain.model.party.PartyRole
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.person.Person
import com.procurement.orchestrator.domain.util.extension.asList
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetOrganizationAction
import org.springframework.stereotype.Component

@Component
class AccessGetOrganizationDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<AccessGetOrganizationDelegate.Parameters, GetOrganizationAction.Result>(
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
    ): Result<Reply<GetOrganizationAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        return accessClient.getOrganization(
            id = commandId,
            params = GetOrganizationAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                role = parameters.role
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        data: GetOrganizationAction.Result
    ): MaybeFail<Fail.Incident> {

        val party = buildParty(data, parameters)
        context.parties = Parties(party.asList())

        return MaybeFail.none()
    }

    class Parameters(val role: PartyRole)

    private fun buildParty(data: GetOrganizationAction.Result, parameters: Parameters) = Party(
        id = data.id,
        name = data.name,
        roles = listOf(parameters.role),
        details = null,
        address = data.address
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
        identifier = data.identifier
            .let { identifier ->
                Identifier(
                    id = identifier.id,
                    legalName = identifier.legalName,
                    scheme = identifier.scheme,
                    uri = identifier.uri
                )
            },
        additionalIdentifiers = data.additionalIdentifiers
            ?.map { additionalIdentifier ->
                Identifier(
                    id = additionalIdentifier.id,
                    legalName = additionalIdentifier.legalName,
                    scheme = additionalIdentifier.scheme,
                    uri = additionalIdentifier.uri
                )
            }
            .orEmpty(),
        contactPoint = data.contactPoint
            .let { contactPoint ->
                ContactPoint(
                    name = contactPoint.name,
                    telephone = contactPoint.telephone,
                    faxNumber = contactPoint.faxNumber,
                    email = contactPoint.email,
                    url = contactPoint.url
                )
            },
        persons = data.persons
            ?.map { person ->
                Person(
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
                    businessFunctions = person.businessFunctions
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
                                documents = businessFunction.documents
                                    ?.map { document ->
                                        Document(
                                            id = document.id,
                                            title = document.title,
                                            url = null,
                                            description = document.description,
                                            documentType = document.documentType,
                                            relatedLots = emptyList(),
                                            datePublished = null,
                                            dateModified = null,
                                            relatedConfirmations = emptyList(),
                                            format = null,
                                            language = null
                                        )
                                    }
                                    .orEmpty()
                            )
                        }
                )
            }
            .orEmpty()
    )
}
