package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.award.Award
import com.procurement.orchestrator.domain.util.extension.asList
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.action.CheckPersonsStructureAction
import org.springframework.stereotype.Component

@Component
class AccessCheckPersonsStructureDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<AccessCheckPersonsStructureDelegate.Parameters, Unit>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        private const val PARAMETER_NAME_LOCATION = "location"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val location: Location = parameterContainer.getString(PARAMETER_NAME_LOCATION)
            .doOnError { return failure(it) }
            .get
            .let {
                Location.orNull(it)
                    ?: return failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = PARAMETER_NAME_LOCATION,
                            expectedValues = Location.allowedElements.keysAsStrings(),
                            actualValue = it
                        )
                    )
            }

        return success(Parameters(location = location))
    }

    override suspend fun execute(
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<Unit>, Fail.Incident> {

        val processInfo = context.processInfo
        val cpid: Cpid = processInfo.cpid
        val ocid: Ocid = processInfo.ocid

        val persons = when (parameters.location) {
            Location.AWARD -> buildPersonsForAward(context)
            else -> failure(Fail.Incident.Bpe(description = "Parameter location: '${parameters.location}' of delegate do not implemented."))
        }
            .doOnError { return failure(it) }
            .get

        return accessClient.checkPersonsStructure(
            params = CheckPersonsStructureAction.Params(
                cpid = cpid,
                ocid = ocid,
                operationType = processInfo.operationType
                    .takeIf {
                        it != OperationTypeProcess.DECLARE_OF_NON_CONFLICT_OF_INTEREST
                    },
                locationOfPersons = parameters.location.key,
                persons = persons
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        data: Unit
    ): MaybeFail<Fail.Incident.Bpmn> = MaybeFail.none()

    private fun buildPersonsForAward(context: CamundaGlobalContext): Result<List<CheckPersonsStructureAction.Params.Person>, Fail.Incident> {
        val awards: List<Award> = context.awards
            .takeIf { it.isNotEmpty() }
            ?: return failure(Fail.Incident.Bpe(description = "The global context does not contain a 'Awards' object."))

        if (awards.size != 1) return failure(
            Fail.Incident.Bpmn.Context.UnConsistency(
                name = "awards",
                description = "It was expected that the attribute 'awards' would have only one value. In fact, the attribute has ${awards.size} meanings"
            )
        )

        val award = awards[0]
        if (award.requirementResponses.size != 1) return failure(
            Fail.Incident.Bpmn.Context.UnConsistency(
                name = "award.requirementResponses",
                description = "It was expected that the attribute 'award.requirementResponses' would have only one value. In fact, the attribute has ${award.requirementResponses.size} meanings"
            )
        )
        val persons = award.requirementResponses[0]
            .responder
            ?.let { responder ->
                CheckPersonsStructureAction.Params.Person(
                    title = responder.title,
                    name = responder.name,
                    identifier = responder.identifier
                        .let { identifier ->
                            CheckPersonsStructureAction.Params.Person.Identifier(
                                scheme = identifier.scheme,
                                id = identifier.id,
                                uri = identifier.uri
                            )
                        },
                    businessFunctions = responder.businessFunctions
                        .map { businessFunction ->
                            CheckPersonsStructureAction.Params.Person.BusinessFunction(
                                id = businessFunction.id,
                                type = businessFunction.type,
                                jobTitle = businessFunction.jobTitle,
                                period = businessFunction.period
                                    ?.let { period ->
                                        CheckPersonsStructureAction.Params.Person.BusinessFunction.Period(
                                            startDate = period.startDate
                                        )
                                    },
                                documents = businessFunction.documents
                                    .map { document ->
                                        CheckPersonsStructureAction.Params.Person.BusinessFunction.Document(
                                            documentType = document.documentType,
                                            id = document.id,
                                            title = document.title,
                                            description = document.description
                                        )
                                    }
                            )
                        }
                )
            }
            ?.asList()
            .orEmpty()

        return success(persons)
    }

    class Parameters(
        val location: Location
    )

    enum class Location(override val key: String) : EnumElementProvider.Key {

        AWARD("award"),
        BUYER("buyer"),
        PROCURING_ENTITY("procuringEntity"),
        SUPPLIERS("suppliers"),
        TENDERERS("tenderers");

        override fun toString(): String = key

        companion object : EnumElementProvider<Location>(info = info())
    }
}
