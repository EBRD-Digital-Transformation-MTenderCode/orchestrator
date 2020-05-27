package com.procurement.orchestrator.infrastructure.configuration

import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.client.ClarificationClient
import com.procurement.orchestrator.application.client.ContractingClient
import com.procurement.orchestrator.application.client.DossierClient
import com.procurement.orchestrator.application.client.EvaluationClient
import com.procurement.orchestrator.application.client.MdmClient
import com.procurement.orchestrator.application.client.NoticeClient
import com.procurement.orchestrator.application.client.QualificationClient
import com.procurement.orchestrator.application.client.RevisionClient
import com.procurement.orchestrator.application.client.StorageClient
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.infrastructure.client.web.OkHttpWebClient
import com.procurement.orchestrator.infrastructure.client.web.WebClient
import com.procurement.orchestrator.infrastructure.client.web.access.HttpAccessClient
import com.procurement.orchestrator.infrastructure.client.web.clarification.HttpClarificationClient
import com.procurement.orchestrator.infrastructure.client.web.contracting.HttpContractingClient
import com.procurement.orchestrator.infrastructure.client.web.dossier.HttpDossierClient
import com.procurement.orchestrator.infrastructure.client.web.evaluation.HttpEvaluationClient
import com.procurement.orchestrator.infrastructure.client.web.mdm.HttpMdmClient
import com.procurement.orchestrator.infrastructure.client.web.notice.HttpNoticeClient
import com.procurement.orchestrator.infrastructure.client.web.qualification.HttpQualificationClient
import com.procurement.orchestrator.infrastructure.client.web.revision.HttpRevisionClient
import com.procurement.orchestrator.infrastructure.client.web.storage.HttpStorageClient
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@EnableConfigurationProperties(ComponentProperties::class)
@Import(
    RepositoryConfiguration::class,
    LoggerConfiguration::class
)
class WebClientConfiguration(
    private val componentProperties: ComponentProperties,
    private val transform: Transform,
    private val repositoryConfiguration: RepositoryConfiguration,
    private val loggerConfiguration: LoggerConfiguration
) {

    @Bean
    fun webClient(): WebClient = OkHttpWebClient(logger = loggerConfiguration.logger(), transform = transform)

    @Bean
    fun accessClient(): AccessClient =
        HttpAccessClient(webClient = webClient(), properties = componentProperties["eAccess"])

    @Bean
    fun revisionClient(): RevisionClient =
        HttpRevisionClient(webClient = webClient(), properties = componentProperties["eRevision"])

    @Bean
    fun storageClient(): StorageClient =
        HttpStorageClient(webClient = webClient(), properties = componentProperties["storage"])

    @Bean
    fun noticeClient(): NoticeClient =
        HttpNoticeClient(webClient = webClient(), properties = componentProperties["eNotice"])

    @Bean
    fun mdmClient(): MdmClient = HttpMdmClient(repositoryConfiguration.errorDescriptionRepository())

    @Bean
    fun dossierClient(): DossierClient =
        HttpDossierClient(webClient = webClient(), properties = componentProperties["eDossier"])

    @Bean
    fun evaluationClient(): EvaluationClient =
        HttpEvaluationClient(webClient = webClient(), properties = componentProperties["eEvaluation"])

    @Bean
    fun contractingClient(): ContractingClient =
        HttpContractingClient(webClient = webClient(), properties = componentProperties["eContracting"])

    @Bean
    fun qualificationClient(): QualificationClient =
        HttpQualificationClient(webClient = webClient(),properties = componentProperties["eQualification"])

    @Bean
    fun clarificationClient():ClarificationClient =
        HttpClarificationClient(webClient = webClient(), properties = componentProperties["eClarification"])
}
