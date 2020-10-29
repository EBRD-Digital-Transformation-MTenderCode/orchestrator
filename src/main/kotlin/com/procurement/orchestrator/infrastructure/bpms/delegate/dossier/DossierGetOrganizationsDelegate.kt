package com.procurement.orchestrator.infrastructure.bpms.delegate.dossier

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.DossierClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
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
import com.procurement.orchestrator.domain.model.organization.datail.Details
import com.procurement.orchestrator.domain.model.organization.datail.MainEconomicActivities
import com.procurement.orchestrator.domain.model.organization.datail.MainEconomicActivity
import com.procurement.orchestrator.domain.model.organization.datail.account.AccountIdentifier
import com.procurement.orchestrator.domain.model.organization.datail.account.AccountIdentifiers
import com.procurement.orchestrator.domain.model.organization.datail.account.BankAccount
import com.procurement.orchestrator.domain.model.organization.datail.account.BankAccounts
import com.procurement.orchestrator.domain.model.organization.datail.legalform.LegalForm
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
import com.procurement.orchestrator.infrastructure.client.web.dossier.DossierCommands
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.GetOrganizationsAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class DossierGetOrganizationsDelegate(
    logger: Logger,
    private val dossierClient: DossierClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, GetOrganizationsAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<GetOrganizationsAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        return dossierClient.getOrganizations(
            id = commandId,
            params = GetOrganizationsAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<GetOrganizationsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(service = ExternalServiceName.DOSSIER, action = DossierCommands.GetOrganizations)
            )

        val buildParties = data.map { organization ->
            buildParty(organization)
        }

        val parties = context.parties
        context.parties = if (parties.isEmpty())
            Parties(buildParties)
        else {
            val receivedPartyByIds = buildParties.associateBy { it.id }
            val knowPartyIds = parties.associateBy { it.id }
            val updatedParties = parties
                .map { party ->
                    receivedPartyByIds[party.id]
                        ?.let { receivedParty ->
                            party updateBy receivedParty
                        }
                        ?: party
                }
            val newParties = buildParties
                .filter { party ->
                    party.id !in knowPartyIds
                }

            Parties(updatedParties + newParties)
        }

        return MaybeFail.none()
    }

    private fun buildParty(organization: GetOrganizationsAction.Result.Organization) = Party(
        id = organization.id,
        name = organization.name,
        roles = PartyRoles(PartyRole.CANDIDATE),
        address = organization.address
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
        identifier = organization.identifier
            .let { identifier ->
                Identifier(
                    id = identifier.id,
                    legalName = identifier.legalName,
                    scheme = identifier.scheme,
                    uri = identifier.uri
                )
            },
        additionalIdentifiers = Identifiers(
            organization.additionalIdentifiers
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
        contactPoint = organization.contactPoint
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
            organization.persons
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
        ),
        details = organization.details
            .let { details: GetOrganizationsAction.Result.Organization.Details ->
                Details(
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
