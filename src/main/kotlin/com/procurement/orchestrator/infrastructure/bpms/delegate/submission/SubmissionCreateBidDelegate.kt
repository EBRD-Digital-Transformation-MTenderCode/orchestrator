package com.procurement.orchestrator.infrastructure.bpms.delegate.submission

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.SubmissionClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetBids
import com.procurement.orchestrator.application.model.context.members.Outcomes
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.submission.SubmissionCommands
import com.procurement.orchestrator.infrastructure.client.web.submission.action.CreateBidAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.convertToDomainObject
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class SubmissionCreateBidDataDelegate(
    logger: Logger,
    private val submissionClient: SubmissionClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, CreateBidAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        Result.success(Unit)

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<CreateBidAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val requestInfo = context.requestInfo

        val bids = context.tryGetBids()
            .orForwardFail { fail -> return fail }

        return submissionClient.createBid(
            id = commandId,
            params = CreateBidAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                owner = requestInfo.owner,
                date = requestInfo.timestamp,
                bids = bids.details.map { bid ->
                    CreateBidAction.Params.Bids.Detail(
                        id = bid.id,
                        value = bid.value
                            ?.let { value ->
                                CreateBidAction.Params.Bids.Detail.Value(
                                    amount = value.amount,
                                    currency = value.currency
                                )
                            },
                        tenderers = bid.tenderers
                            .map { tenderer ->
                                CreateBidAction.Params.Bids.Detail.Tenderer(
                                    id = tenderer.id,
                                    name = tenderer.name,
                                    identifier = tenderer.identifier
                                        ?.let { identifier ->
                                            CreateBidAction.Params.Bids.Detail.Tenderer.Identifier(
                                                id = identifier.id,
                                                legalName = identifier.legalName,
                                                scheme = identifier.scheme,
                                                uri = identifier.uri
                                            )
                                        },
                                    additionalIdentifiers = tenderer.additionalIdentifiers
                                        .map { additionalIdentifier ->
                                            CreateBidAction.Params.Bids.Detail.Tenderer.AdditionalIdentifier(
                                                id = additionalIdentifier.id,
                                                legalName = additionalIdentifier.legalName,
                                                scheme = additionalIdentifier.scheme,
                                                uri = additionalIdentifier.uri
                                            )
                                        },
                                    address = tenderer.address
                                        ?.let { address ->
                                            CreateBidAction.Params.Bids.Detail.Tenderer.Address(
                                                streetAddress = address.streetAddress,
                                                postalCode = address.postalCode,
                                                addressDetails = address.addressDetails
                                                    ?.let { addressDetails ->
                                                        CreateBidAction.Params.Bids.Detail.Tenderer.Address.AddressDetails(
                                                            country = addressDetails.country
                                                                .let { country ->
                                                                    CreateBidAction.Params.Bids.Detail.Tenderer.Address.AddressDetails.Country(
                                                                        scheme = country.scheme,
                                                                        id = country.id,
                                                                        description = country.description,
                                                                        uri = country.uri
                                                                    )
                                                                },
                                                            region = addressDetails.region
                                                                .let { region ->
                                                                    CreateBidAction.Params.Bids.Detail.Tenderer.Address.AddressDetails.Region(
                                                                        scheme = region.scheme,
                                                                        id = region.id,
                                                                        description = region.description,
                                                                        uri = region.uri
                                                                    )
                                                                },
                                                            locality = addressDetails.locality
                                                                .let { locality ->
                                                                    CreateBidAction.Params.Bids.Detail.Tenderer.Address.AddressDetails.Locality(
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
                                    contactPoint = tenderer.contactPoint
                                        ?.let { contactPoint ->
                                            CreateBidAction.Params.Bids.Detail.Tenderer.ContactPoint(
                                                name = contactPoint.name,
                                                email = contactPoint.email,
                                                telephone = contactPoint.telephone,
                                                faxNumber = contactPoint.faxNumber,
                                                url = contactPoint.url
                                            )
                                        },
                                    persones = tenderer.persons
                                        .map { person ->
                                            CreateBidAction.Params.Bids.Detail.Tenderer.Persone(
                                                id = person.id,
                                                identifier = person.identifier
                                                    ?.let { identifier ->
                                                        CreateBidAction.Params.Bids.Detail.Tenderer.Persone.Identifier(
                                                            scheme = identifier.scheme,
                                                            id = identifier.id,
                                                            uri = identifier.uri
                                                        )
                                                    },
                                                name = person.name,
                                                title = person.title,
                                                businessFunctions = person.businessFunctions
                                                    .map { businessFunction ->
                                                        CreateBidAction.Params.Bids.Detail.Tenderer.Persone.BusinessFunction(
                                                            id = businessFunction.id,
                                                            type = businessFunction.type,
                                                            jobTitle = businessFunction.jobTitle,
                                                            period = businessFunction.period?.let { period ->
                                                                CreateBidAction.Params.Bids.Detail.Tenderer.Persone.BusinessFunction.Period(
                                                                    startDate = period.startDate
                                                                )
                                                            },
                                                            documents = businessFunction.documents
                                                                .map { document ->
                                                                    CreateBidAction.Params.Bids.Detail.Tenderer.Persone.BusinessFunction.Document(
                                                                        documentType = document.documentType,
                                                                        id = document.id,
                                                                        title = document.title,
                                                                        description = document.description
                                                                    )
                                                                }
                                                        )
                                                    }
                                            )
                                        },
                                    details = tenderer.details
                                        ?.let { detail ->
                                            CreateBidAction.Params.Bids.Detail.Tenderer.Details(
                                                typeOfSupplier = detail.typeOfSupplier,
                                                mainEconomicActivities = detail.mainEconomicActivities
                                                    .map { mainEconomicActivity ->
                                                        CreateBidAction.Params.Bids.Detail.Tenderer.Details.MainEconomicActivity(
                                                            id = mainEconomicActivity.id,
                                                            scheme = mainEconomicActivity.scheme,
                                                            uri = mainEconomicActivity.uri,
                                                            description = mainEconomicActivity.description
                                                        )
                                                    },
                                                scale = detail.scale,
                                                permits = detail.permits.map { permit ->
                                                    CreateBidAction.Params.Bids.Detail.Tenderer.Details.Permit(
                                                        scheme = permit.scheme,
                                                        id = permit.id,
                                                        url = permit.url,
                                                        permitDetails = permit.permitDetails
                                                            ?.let { permitDetails ->
                                                                CreateBidAction.Params.Bids.Detail.Tenderer.Details.Permit.PermitDetails(
                                                                    issuedBy = permitDetails.issuedBy
                                                                        ?.let { issuedBy ->
                                                                            CreateBidAction.Params.Bids.Detail.Tenderer.Details.Permit.PermitDetails.IssuedBy(
                                                                                id = issuedBy.id,
                                                                                name = issuedBy.name
                                                                            )
                                                                        },
                                                                    issuedThought = permitDetails.issuedThought
                                                                        ?.let { issuedThought ->
                                                                            CreateBidAction.Params.Bids.Detail.Tenderer.Details.Permit.PermitDetails.IssuedThought(
                                                                                id = issuedThought.id,
                                                                                name = issuedThought.name
                                                                            )
                                                                        },
                                                                    validityPeriod = permitDetails.validityPeriod
                                                                        ?.let { validityPeriod ->
                                                                            CreateBidAction.Params.Bids.Detail.Tenderer.Details.Permit.PermitDetails.ValidityPeriod(
                                                                                startDate = validityPeriod.startDate,
                                                                                endDate = validityPeriod.startDate
                                                                            )
                                                                        }
                                                                )
                                                            }
                                                    )
                                                },
                                                bankAccounts = detail.bankAccounts
                                                    .map { bankAccount ->
                                                        CreateBidAction.Params.Bids.Detail.Tenderer.Details.BankAccount(
                                                            description = bankAccount.description,
                                                            bankName = bankAccount.bankName,
                                                            address = bankAccount.address
                                                                ?.let { address ->
                                                                    CreateBidAction.Params.Bids.Detail.Tenderer.Details.BankAccount.Address(
                                                                        streetAddress = address.streetAddress,
                                                                        postalCode = address.postalCode,
                                                                        addressDetails = address.addressDetails
                                                                            ?.let { addressDetails ->
                                                                                CreateBidAction.Params.Bids.Detail.Tenderer.Details.BankAccount.Address.AddressDetails(
                                                                                    country = addressDetails.country
                                                                                        .let { country ->
                                                                                            CreateBidAction.Params.Bids.Detail.Tenderer.Details.BankAccount.Address.AddressDetails.Country(
                                                                                                scheme = country.scheme,
                                                                                                id = country.id,
                                                                                                description = country.description,
                                                                                                uri = country.uri
                                                                                            )
                                                                                        },
                                                                                    region = addressDetails.region
                                                                                        .let { region ->
                                                                                            CreateBidAction.Params.Bids.Detail.Tenderer.Details.BankAccount.Address.AddressDetails.Region(
                                                                                                scheme = region.scheme,
                                                                                                id = region.id,
                                                                                                description = region.description,
                                                                                                uri = region.uri
                                                                                            )
                                                                                        },
                                                                                    locality = addressDetails.locality
                                                                                        .let { locality ->
                                                                                            CreateBidAction.Params.Bids.Detail.Tenderer.Details.BankAccount.Address.AddressDetails.Locality(
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
                                                                ?.let { accountIdentifier ->
                                                                    CreateBidAction.Params.Bids.Detail.Tenderer.Details.BankAccount.Identifier(
                                                                        scheme = accountIdentifier.scheme,
                                                                        id = accountIdentifier.id
                                                                    )
                                                                },
                                                            accountIdentification = bankAccount.accountIdentification
                                                                ?.let { accountIdentifier ->
                                                                    CreateBidAction.Params.Bids.Detail.Tenderer.Details.BankAccount.AccountIdentification(
                                                                        scheme = accountIdentifier.scheme,
                                                                        id = accountIdentifier.id
                                                                    )

                                                                },
                                                            additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers
                                                                .map { accountIdentifier ->
                                                                    CreateBidAction.Params.Bids.Detail.Tenderer.Details.BankAccount.AdditionalAccountIdentifier(
                                                                        scheme = accountIdentifier.scheme,
                                                                        id = accountIdentifier.id
                                                                    )
                                                                }
                                                        )
                                                    },
                                                legalForm = detail.legalForm
                                                    ?.let { legalForm ->
                                                        CreateBidAction.Params.Bids.Detail.Tenderer.Details.LegalForm(
                                                            scheme = legalForm.scheme,
                                                            id = legalForm.id,
                                                            description = legalForm.description,
                                                            uri = legalForm.uri
                                                        )
                                                    }
                                            )
                                        }
                                )
                            },
                        relatedLots = bid.relatedLots,
                        documents = bid.documents
                            .map { document ->
                                CreateBidAction.Params.Bids.Detail.Document(
                                    documentType = document.documentType,
                                    id = document.id,
                                    title = document.title,
                                    description = document.description,
                                    relatedLots = document.relatedLots
                                )
                            },
                        items = bid.items
                            .map { item ->
                                CreateBidAction.Params.Bids.Detail.Item(
                                    id = item.id,
                                    unit = item.unit?.let { unit ->
                                        CreateBidAction.Params.Bids.Detail.Item.Unit(
                                            id = unit.id,
                                            name = unit.name,
                                            value = unit.value
                                                ?.let { value ->
                                                    CreateBidAction.Params.Bids.Detail.Item.Unit.Value(
                                                        amount = value.amount,
                                                        currency = value.currency
                                                    )
                                                }
                                        )
                                    }
                                )
                            },
                        requirementResponses = bid.requirementResponses
                            .map { requirementResponse ->
                                CreateBidAction.Params.Bids.Detail.RequirementResponse(
                                    id = requirementResponse.id,
                                    value = requirementResponse.value,
                                    period = requirementResponse.period
                                        ?.let { period ->
                                            CreateBidAction.Params.Bids.Detail.RequirementResponse.Period(
                                                startDate = period.startDate,
                                                endDate = period.endDate
                                            )
                                        },
                                    requirement = requirementResponse.requirement
                                        ?.let { requirement ->
                                            CreateBidAction.Params.Bids.Detail.RequirementResponse.Requirement(
                                                requirement.id
                                            )
                                        }
                                )
                            }
                    )
                }.let { CreateBidAction.Params.Bids(it) }
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<CreateBidAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.SUBMISSION,
                    action = SubmissionCommands.CreateBid
                )
            )

        context.bids = data.bids.convertToDomainObject()
        context.outcomes = createOutcomes(context, data)

        return MaybeFail.none()
    }

    private fun createOutcomes(context: CamundaGlobalContext, data: CreateBidAction.Result): Outcomes {
        val platformId = context.requestInfo.platformId
        val outcomes = context.outcomes ?: Outcomes()
        val details = outcomes[platformId] ?: Outcomes.Details()

        val newOutcomes = Outcomes.Details.Bids(
            details = data.bids.details
                .map { bid -> Outcomes.Details.Bids.Details(id = bid.id, token = data.token) }
        )

        val updatedDetails = details.copy(bids = newOutcomes)
        outcomes[platformId] = updatedDetails
        return outcomes
    }
}
