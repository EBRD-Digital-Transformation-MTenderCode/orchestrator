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
import com.procurement.orchestrator.domain.model.identifier.Identifier
import com.procurement.orchestrator.domain.model.identifier.Identifiers
import com.procurement.orchestrator.domain.model.party.Party
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.FindSubmissionsForOpeningAction
import org.springframework.stereotype.Component

@Component
class DossierGetSubmissionsForPcrDelegate(
    logger: Logger,
    private val dossierClient: DossierClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, FindSubmissionsForOpeningAction.Result>(
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
    ): Result<Reply<FindSubmissionsForOpeningAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val requestInfo = context.requestInfo
        return dossierClient.findSubmissionsForOpening(
            id = commandId,
            params = FindSubmissionsForOpeningAction.Params(
                cpid = processInfo.relatedProcess!!.cpid,
                ocid = processInfo.relatedProcess.ocid!!,
                pmd = processInfo.pmd,
                country = requestInfo.country,
                operationType = processInfo.operationType
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<FindSubmissionsForOpeningAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.none()

        val parties = data.map { submission ->
            submission.candidates.map { candidate ->
                Party(
                    id = candidate.id,
                    name = candidate.name,
                    additionalIdentifiers = candidate.additionalIdentifiers
                        ?.map { additionalIdentifier ->
                            Identifier(
                                id = additionalIdentifier.id,
                                scheme = additionalIdentifier.scheme,
                                legalName = additionalIdentifier.legalName,
                                uri = additionalIdentifier.uri
                            )

                        }.orEmpty()
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
                    contactPoint = 
                    )
            }
        }

        return MaybeFail.none()
    }
}
