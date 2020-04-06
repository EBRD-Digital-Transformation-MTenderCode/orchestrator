package com.procurement.orchestrator.infrastructure.configuration

import com.procurement.orchestrator.application.service.ProcessService
import com.procurement.orchestrator.application.service.ProcessServiceImpl
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.application.service.cancellation.CancellationService
import com.procurement.orchestrator.application.service.cancellation.CancellationServiceImpl
import com.procurement.orchestrator.application.service.response.RequirementResponseService
import com.procurement.orchestrator.application.service.response.RequirementResponseServiceImpl
import com.procurement.orchestrator.infrastructure.configuration.property.UriProperties
import org.camunda.bpm.engine.RuntimeService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@EnableConfigurationProperties(UriProperties::class)
@Import(
    RepositoryConfiguration::class
)
class ServiceConfiguration(
    private val transform: Transform,
    private val repositoryConfiguration: RepositoryConfiguration,
    private val runtimeService: RuntimeService
) {

    @Bean
    fun processService(): ProcessService = ProcessServiceImpl(
        transform = transform,
        processDefinitionRepository = repositoryConfiguration.processDefinitionRepository(),
        processContextRepository = repositoryConfiguration.processContextRepository(),
        oldProcessContextRepository = repositoryConfiguration.oldProcessContextRepository(),
        processInitializerRepository = repositoryConfiguration.processInitializerRepository(),
        runtimeService = runtimeService
    )

    @Bean
    fun cancellationService(): CancellationService =
        CancellationServiceImpl(
            transform = transform,
            processService = processService(),
            requestRepository = repositoryConfiguration.requestRepository(),
            ruleRepository = repositoryConfiguration.ruleRepository()
        )

    @Bean
    fun requirementResponseService(): RequirementResponseService = RequirementResponseServiceImpl(
        transform = transform,
        processService = processService(),
        requestRepository = repositoryConfiguration.requestRepository(),
        ruleRepository = repositoryConfiguration.ruleRepository()
    )
}
