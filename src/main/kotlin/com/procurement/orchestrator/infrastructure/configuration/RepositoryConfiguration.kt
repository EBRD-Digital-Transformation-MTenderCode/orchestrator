package com.procurement.orchestrator.infrastructure.configuration

import com.datastax.driver.core.Session
import com.procurement.orchestrator.application.repository.OldProcessContextRepository
import com.procurement.orchestrator.application.repository.ProcessDefinitionRepository
import com.procurement.orchestrator.application.repository.ProcessInitializerRepository
import com.procurement.orchestrator.application.repository.RuleRepository
import com.procurement.orchestrator.infrastructure.bpms.repository.ErrorDescriptionRepository
import com.procurement.orchestrator.infrastructure.bpms.repository.NoticeQueueRepository
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.bpms.repository.RequestRepository
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import com.procurement.orchestrator.infrastructure.repository.CassandraErrorDescriptionRepository
import com.procurement.orchestrator.infrastructure.repository.CassandraNoticeQueueRepository
import com.procurement.orchestrator.infrastructure.repository.CassandraOldProcessContextRepository
import com.procurement.orchestrator.infrastructure.repository.CassandraOperationStepRepository
import com.procurement.orchestrator.infrastructure.repository.CassandraProcessDefinitionRepository
import com.procurement.orchestrator.infrastructure.repository.CassandraProcessInitializerRepository
import com.procurement.orchestrator.infrastructure.repository.CassandraRequestRepository
import com.procurement.orchestrator.infrastructure.repository.CassandraRuleRepository
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(ComponentProperties::class)
class RepositoryConfiguration(private val session: Session) {

    @Bean
    fun operationStepRepository(): OperationStepRepository = CassandraOperationStepRepository(session = session)

    @Bean
    fun noticeQueueRepository(): NoticeQueueRepository = CassandraNoticeQueueRepository(session = session)

    @Bean
    fun processDefinitionRepository(): ProcessDefinitionRepository =
        CassandraProcessDefinitionRepository(session = session)

    @Bean
    fun ruleRepository(): RuleRepository = CassandraRuleRepository(session = session)

    @Bean
    fun requestRepository(): RequestRepository = CassandraRequestRepository(session = session)

    @Bean
    fun processInitializerRepository(): ProcessInitializerRepository =
        CassandraProcessInitializerRepository(session = session)

    @Bean
    fun oldProcessContextRepository(): OldProcessContextRepository =
        CassandraOldProcessContextRepository(session = session)

    @Bean
    fun errorDescriptionRepository(): ErrorDescriptionRepository =
        CassandraErrorDescriptionRepository(session = session)
}
