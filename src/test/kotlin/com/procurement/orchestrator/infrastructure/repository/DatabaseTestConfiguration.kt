package com.procurement.orchestrator.infrastructure.repository

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.wait.strategy.Wait

@TestConfiguration
class DatabaseTestConfiguration {
    @Bean
    fun container() = CassandraTestContainer("3.11")
        .apply {
            setWaitStrategy(Wait.forListeningPort())
            start()
        }
}
