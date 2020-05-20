package com.procurement.orchestrator.infrastructure.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.infrastructure.bind.jackson.configuration
import com.procurement.orchestrator.infrastructure.service.JacksonJsonTransform
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TransformConfiguration {

    @Bean
    fun transform(): Transform = JacksonJsonTransform(mapper = ObjectMapper().apply { configuration() })
}
