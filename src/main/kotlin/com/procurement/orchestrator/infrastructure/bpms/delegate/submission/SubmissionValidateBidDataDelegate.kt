package com.procurement.orchestrator.infrastructure.bpms.delegate.submission

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.SubmissionClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetBids
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.submission.action.ValidateBidDataAction
import org.springframework.stereotype.Component

@Component
class SubmissionValidateBidDataDelegate(
    logger: Logger,
    private val submissionClient: SubmissionClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, Unit>(
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
    ): Result<Reply<Unit>, Fail.Incident> {

        val bids = context.tryGetBids()
            .orForwardFail { fail -> return fail }
        val tender = context.tryGetTender()
            .orForwardFail { fail -> return fail }

        return submissionClient.validateBidData(
            id = commandId,
            params = ValidateBidDataAction.Params(
                cpid = context.processInfo.cpid!!,
                bids = bids.details.map { bid ->
                    ValidateBidDataAction.Params.Bids.Detail(
                        id = bid.id,
                        value = bid.value
                            ?.let { value ->
                                ValidateBidDataAction.Params.Bids.Detail.Value(
                                    amount = value.amount,
                                    currency = value.currency
                                )
                            },
                        tenderers = bid.tenderers
                            .map { tenderer ->
                                ValidateBidDataAction.Params.Bids.Detail.Tenderer(
                                    id = tenderer.id,
                                    name = tenderer.name,
                                    identifier = tenderer.identifier
                                        ?.let { identifier ->
                                            ValidateBidDataAction.Params.Bids.Detail.Tenderer.Identifier(
                                                id = identifier.id,
                                                legalName = identifier.legalName,
                                                scheme = identifier.scheme,
                                                uri = identifier.uri
                                            )
                                        },
                                    additionalIdentifiers = tenderer.additionalIdentifiers
                                        .map { additionalIdentifier ->
                                            ValidateBidDataAction.Params.Bids.Detail.Tenderer.AdditionalIdentifier(
                                                id = additionalIdentifier.id,
                                                legalName = additionalIdentifier.legalName,
                                                scheme = additionalIdentifier.scheme,
                                                uri = additionalIdentifier.uri
                                            )
                                        },
                                    address = tenderer.address
                                        ?.let { address ->
                                            ValidateBidDataAction.Params.Bids.Detail.Tenderer.Address(
                                                streetAddress = address.streetAddress,
                                                postalCode = address.postalCode,
                                                addressDetails = address.addressDetails
                                                    ?.let { addressDetails ->
                                                        ValidateBidDataAction.Params.Bids.Detail.Tenderer.Address.AddressDetails(
                                                            country = addressDetails.country
                                                                .let { country ->
                                                                    ValidateBidDataAction.Params.Bids.Detail.Tenderer.Address.AddressDetails.Country(
                                                                        scheme = country.scheme,
                                                                        id = country.id,
                                                                        description = country.description
                                                                    )
                                                                },
                                                            region = addressDetails.region
                                                                .let { region ->
                                                                    ValidateBidDataAction.Params.Bids.Detail.Tenderer.Address.AddressDetails.Region(
                                                                        scheme = region.scheme,
                                                                        id = region.id,
                                                                        description = region.description
                                                                    )
                                                                },
                                                            locality = addressDetails.locality
                                                                .let { locality ->
                                                                    ValidateBidDataAction.Params.Bids.Detail.Tenderer.Address.AddressDetails.Locality(
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
                                        ?.let { contactPoint ->
                                            ValidateBidDataAction.Params.Bids.Detail.Tenderer.ContactPoint(
                                                name = contactPoint.name,
                                                email = contactPoint.email,
                                                telephone = contactPoint.telephone,
                                                faxNumber = contactPoint.faxNumber,
                                                url = contactPoint.url
                                            )
                                        },
                                    persones = tenderer.persons
                                        .map { person ->
                                            ValidateBidDataAction.Params.Bids.Detail.Tenderer.Persone(
                                                id = person.id,
                                                identifier = person.identifier
                                                    ?.let { identifier ->
                                                        ValidateBidDataAction.Params.Bids.Detail.Tenderer.Persone.Identifier(
                                                            scheme = identifier.scheme,
                                                            id = identifier.id,
                                                            uri = identifier.uri
                                                        )
                                                    },
                                                name = person.name,
                                                title = person.title,
                                                businessFunctions = person.businessFunctions
                                                    .map { businessFunction ->
                                                        ValidateBidDataAction.Params.Bids.Detail.Tenderer.Persone.BusinessFunction(
                                                            id = businessFunction.id,
                                                            type = businessFunction.type,
                                                            jobTitle = businessFunction.jobTitle,
                                                            period = businessFunction.period?.let { period ->
                                                                ValidateBidDataAction.Params.Bids.Detail.Tenderer.Persone.BusinessFunction.Period(
                                                                    startDate = period.startDate
                                                                )
                                                            },
                                                            documents = businessFunction.documents
                                                                .map { document ->
                                                                    ValidateBidDataAction.Params.Bids.Detail.Tenderer.Persone.BusinessFunction.Document(
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
                                            ValidateBidDataAction.Params.Bids.Detail.Tenderer.Details(
                                                typeOfSupplier = detail.typeOfSupplier,
                                                mainEconomicActivities = detail.mainEconomicActivities
                                                    .map { mainEconomicActivity ->
                                                        ValidateBidDataAction.Params.Bids.Detail.Tenderer.Details.MainEconomicActivity(
                                                            id = mainEconomicActivity.id,
                                                            scheme = mainEconomicActivity.scheme,
                                                            uri = mainEconomicActivity.uri,
                                                            description = mainEconomicActivity.description
                                                        )
                                                    },
                                                scale = detail.scale,
                                                permits = detail.permits.map { permit ->
                                                    ValidateBidDataAction.Params.Bids.Detail.Tenderer.Details.Permit(
                                                        scheme = permit.scheme,
                                                        id = permit.id,
                                                        url = permit.url,
                                                        permitDetails = permit.permitDetails
                                                            ?.let { permitDetails ->
                                                                ValidateBidDataAction.Params.Bids.Detail.Tenderer.Details.Permit.PermitDetails(
                                                                    issuedBy = permitDetails.issuedBy
                                                                        ?.let { issuedBy ->
                                                                            ValidateBidDataAction.Params.Bids.Detail.Tenderer.Details.Permit.PermitDetails.IssuedBy(
                                                                                id = issuedBy.id,
                                                                                name = issuedBy.name
                                                                            )
                                                                        },
                                                                    issuedThought = permitDetails.issuedThought
                                                                        ?.let { issuedThought ->
                                                                            ValidateBidDataAction.Params.Bids.Detail.Tenderer.Details.Permit.PermitDetails.IssuedThought(
                                                                                id = issuedThought.id,
                                                                                name = issuedThought.name
                                                                            )
                                                                        },
                                                                    validityPeriod = permitDetails.validityPeriod
                                                                        ?.let { validityPeriod ->
                                                                            ValidateBidDataAction.Params.Bids.Detail.Tenderer.Details.Permit.PermitDetails.ValidityPeriod(
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
                                                        ValidateBidDataAction.Params.Bids.Detail.Tenderer.Details.BankAccount(
                                                            description = bankAccount.description,
                                                            bankName = bankAccount.bankName,
                                                            address = bankAccount.address
                                                                ?.let { address ->
                                                                    ValidateBidDataAction.Params.Bids.Detail.Tenderer.Details.BankAccount.Address(
                                                                        streetAddress = address.streetAddress,
                                                                        postalCode = address.postalCode,
                                                                        addressDetails = address.addressDetails
                                                                            ?.let { addressDetails ->
                                                                                ValidateBidDataAction.Params.Bids.Detail.Tenderer.Details.BankAccount.Address.AddressDetails(
                                                                                    country = addressDetails.country
                                                                                        .let { country ->
                                                                                            ValidateBidDataAction.Params.Bids.Detail.Tenderer.Details.BankAccount.Address.AddressDetails.Country(
                                                                                                scheme = country.scheme,
                                                                                                id = country.id,
                                                                                                description = country.description
                                                                                            )
                                                                                        },
                                                                                    region = addressDetails.region
                                                                                        .let { region ->
                                                                                            ValidateBidDataAction.Params.Bids.Detail.Tenderer.Details.BankAccount.Address.AddressDetails.Region(
                                                                                                scheme = region.scheme,
                                                                                                id = region.id,
                                                                                                description = region.description
                                                                                            )
                                                                                        },
                                                                                    locality = addressDetails.locality
                                                                                        .let { locality ->
                                                                                            ValidateBidDataAction.Params.Bids.Detail.Tenderer.Details.BankAccount.Address.AddressDetails.Locality(
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
                                                                ?.let { accountIdentifier ->
                                                                    ValidateBidDataAction.Params.Bids.Detail.Tenderer.Details.BankAccount.Identifier(
                                                                        scheme = accountIdentifier.scheme,
                                                                        id = accountIdentifier.id
                                                                    )
                                                                },
                                                            accountIdentification = bankAccount.accountIdentification
                                                                ?.let { accountIdentifier ->
                                                                    ValidateBidDataAction.Params.Bids.Detail.Tenderer.Details.BankAccount.AccountIdentification(
                                                                        scheme = accountIdentifier.scheme,
                                                                        id = accountIdentifier.id
                                                                    )

                                                                },
                                                            additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers
                                                                .map { accountIdentifier ->
                                                                    ValidateBidDataAction.Params.Bids.Detail.Tenderer.Details.BankAccount.AdditionalAccountIdentifier(
                                                                        scheme = accountIdentifier.scheme,
                                                                        id = accountIdentifier.id
                                                                    )
                                                                }
                                                        )
                                                    },
                                                legalForm = detail.legalForm
                                                    ?.let { legalForm ->
                                                        ValidateBidDataAction.Params.Bids.Detail.Tenderer.Details.LegalForm(
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
                                ValidateBidDataAction.Params.Bids.Detail.Document(
                                    documentType = document.documentType,
                                    id = document.id,
                                    title = document.title,
                                    description = document.description,
                                    relatedLots = document.relatedLots
                                )
                            },
                        items = bid.items
                            .map { item ->
                                ValidateBidDataAction.Params.Bids.Detail.Item(
                                    id = item.id,
                                    unit = item.unit?.let { unit ->
                                        ValidateBidDataAction.Params.Bids.Detail.Item.Unit(
                                            id = unit.id,
                                            value = unit.value
                                                ?.let { value ->
                                                    ValidateBidDataAction.Params.Bids.Detail.Item.Unit.Value(
                                                        amount = value.amount,
                                                        currency = value.currency
                                                    )
                                                }
                                        )
                                    }
                                )
                            }
                    )
                }
                    .let { ValidateBidDataAction.Params.Bids(it) },
                tender = ValidateBidDataAction.Params.Tender(
                    value = tender.value
                        ?.let { value ->
                            ValidateBidDataAction.Params.Tender.Value(
                                value.currency
                            )
                        },
                    procurementMethodModalities = tender.procurementMethodModalities,
                    items = tender.items.map { item ->
                        ValidateBidDataAction.Params.Tender.Item(
                            id = item.id,
                            unit = item.unit?.let { ValidateBidDataAction.Params.Tender.Item.Unit(it.id) }
                        )
                    }
                )
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<Unit>
    ): MaybeFail<Fail.Incident.Bpmn> = MaybeFail.none()
}

