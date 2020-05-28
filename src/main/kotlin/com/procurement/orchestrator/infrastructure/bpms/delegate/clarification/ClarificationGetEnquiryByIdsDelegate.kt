package com.procurement.orchestrator.infrastructure.bpms.delegate.clarification

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.ClarificationClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getEnquiriesIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.tryGetPartyIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
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
import com.procurement.orchestrator.domain.model.enquiry.Enquiries
import com.procurement.orchestrator.domain.model.enquiry.Enquiry
import com.procurement.orchestrator.domain.model.identifier.Identifier
import com.procurement.orchestrator.domain.model.identifier.Identifiers
import com.procurement.orchestrator.domain.model.organization.ContactPoint
import com.procurement.orchestrator.domain.model.organization.OrganizationReference
import com.procurement.orchestrator.domain.model.organization.datail.Details
import com.procurement.orchestrator.domain.model.party.Parties
import com.procurement.orchestrator.domain.model.party.Party
import com.procurement.orchestrator.domain.model.party.PartyRole
import com.procurement.orchestrator.domain.model.party.PartyRoles
import com.procurement.orchestrator.domain.util.extension.getNewElements
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.clarification.ClarificationCommands
import com.procurement.orchestrator.infrastructure.client.web.clarification.action.GetEnquiryByIdsAction
import org.springframework.stereotype.Component

@Component
class ClarificationGetEnquiryByIdsDelegate(
    logger: Logger,
    private val clarificationClient: ClarificationClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, GetEnquiryByIdsAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> {
        return Unit.asSuccess()
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<GetEnquiryByIdsAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo

        val enquiryIds = context.tryGetTender()
            .orForwardFail { fail -> return fail }
            .getEnquiriesIfNotEmpty()
            .orForwardFail { fail -> return fail }
            .map { it.id }

        return clarificationClient.getEnquiryByIds(
            id = commandId,
            params = GetEnquiryByIdsAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                enquiryIds = enquiryIds
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<GetEnquiryByIdsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val contextParties = context.tryGetPartyIfNotEmpty()
            .orReturnFail { fail -> return MaybeFail.fail(fail) }

        val contextTender = context.tryGetTender()
            .orReturnFail { fail -> return MaybeFail.fail(fail) }

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = "eClarification",
                    action = ClarificationCommands.GetEnquiryByIds
                )
            )


        val requestParty = data.map { enquiry -> enquiry.convertToParty() }
        val newParty = getNewElements(received = requestParty, known = contextParties)

        val requestEnquiries = data.map { reEnquiry -> reEnquiry.convertToEnquiry() }
        val newEnquiries = getNewElements(received = requestEnquiries,known = contextTender.enquiries)
        val updatedTender = contextTender.copy(enquiries = Enquiries(contextTender.enquiries + newEnquiries))

        context.parties = Parties(contextParties + newParty)
        context.tender = updatedTender
        return MaybeFail.none()
    }

    private fun GetEnquiryByIdsAction.Result.Enquiry.convertToEnquiry(): Enquiry = Enquiry(
        id = this.id,
        description = this.description,
        date = this.date,
        title = this.title,
        answer = this.answer,
        author = this.author
            .let { orgRef -> OrganizationReference(id = orgRef.id, name = orgRef.name) },
        dateAnswered = this.dateAnswer,
        relatedLot = this.relatedLot
    )

    fun GetEnquiryByIdsAction.Result.Enquiry.convertToParty(): Party =
        Party(
            id = this.author.id,
            details = this.author.details
                .let { details ->
                    Details(scale = details.scale)
                },
            address = this.author.address
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
                                                uri = country.uri,
                                                id = country.id,
                                                description = country.description,
                                                scheme = country.scheme
                                            )
                                        },
                                    locality = addressDetails.locality
                                        .let { country ->
                                            LocalityDetails(
                                                uri = country.uri,
                                                id = country.id,
                                                description = country.description,
                                                scheme = country.scheme
                                            )
                                        },
                                    region = addressDetails.region
                                        .let { country ->
                                            RegionDetails(
                                                uri = country.uri,
                                                id = country.id,
                                                description = country.description,
                                                scheme = country.scheme
                                            )
                                        }
                                )
                            }
                    )
                },
            name = this.author.name,
            additionalIdentifiers = Identifiers(
                values = this.author.additionalIdentifiers
                    ?.map { identifier ->
                        Identifier(
                            id = identifier.id,
                            uri = identifier.uri,
                            scheme = identifier.scheme,
                            legalName = identifier.scheme
                        )
                    } ?: emptyList()
            ),
            contactPoint = this.author.contactPoint
                .let { contactPoint ->
                    ContactPoint(
                        name = contactPoint.name,
                        url = contactPoint.url,
                        email = contactPoint.email,
                        faxNumber = contactPoint.faxNumber,
                        telephone = contactPoint.telephone
                    )
                },
            identifier = this.author.identifier
                .let { identifier ->
                    Identifier(
                        scheme = identifier.scheme,
                        legalName = identifier.legalName,
                        uri = identifier.uri,
                        id = identifier.id
                    )
                },
            roles = PartyRoles(PartyRole.AUTHOR)
        )
}
