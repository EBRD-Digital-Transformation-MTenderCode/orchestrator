package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer

import com.procurement.orchestrator.application.model.context.CamundaContext
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.repository.ProcessInitializerRepository
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.model.contract.Contract
import com.procurement.orchestrator.domain.model.contract.ContractId
import com.procurement.orchestrator.domain.model.contract.Contracts
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import org.springframework.stereotype.Component

@Component
class BpeInitializeCreateContractProcessDelegate(
    val logger: Logger,
    transform: Transform,
    operationStepRepository: OperationStepRepository,
    processInitializerRepository: ProcessInitializerRepository
) : AbstractInitializeProcessDelegate(logger, transform, operationStepRepository, processInitializerRepository) {

    override fun updateGlobalContext(camundaContext: CamundaContext, globalContext: CamundaGlobalContext): MaybeFail<Fail> {
        val payload: CreateContractProcess.Request.Payload =
            parsePayload(camundaContext.request.payload, CreateContractProcess.Request.Payload::class.java)
                .orReturnFail { return MaybeFail.fail(it) }

        val receivedContracts = payload.contracts
            .map { contract -> Contract(id = ContractId.create(contract.id)) }
            .let { Contracts(it) }

        globalContext.contracts = receivedContracts

        return MaybeFail.none()
    }
}
