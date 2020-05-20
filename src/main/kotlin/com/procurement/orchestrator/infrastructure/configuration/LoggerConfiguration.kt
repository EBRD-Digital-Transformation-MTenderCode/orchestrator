package com.procurement.orchestrator.infrastructure.configuration

import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.infrastructure.service.CustomLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LoggerConfiguration {

    @Bean
    fun logger(): Logger = CustomLogger()
}
