package com.procurement.orchestrator.infrastructure.client.kafka

import com.procurement.orchestrator.application.client.NotificatorClient
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.bpms.model.ResultContext
import com.procurement.orchestrator.infrastructure.client.retry.RetryInfo
import com.procurement.orchestrator.infrastructure.extension.kafka.trySend
import kotlinx.coroutines.delay
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import kotlin.coroutines.coroutineContext

abstract class AbstractKafkaNotificatorClient<T>(
    private val logger: Logger,
    private val transform: Transform
) : NotificatorClient<T> {

    abstract val topic: String
    abstract val retryInfo: RetryInfo
    abstract val producer: KafkaProducer<String, String>

    final override suspend fun send(message: T): MaybeFail<Fail.Incident> {
        val value = transform.trySerialization(message)
            .doOnError { return MaybeFail.fail(it) }
            .get

        val ctx = coroutineContext[ResultContext.Key]!!
        ctx.request(value)

        val record = ProducerRecord<String, String>(topic, value)
        return execute(record, retryInfo).asMaybeFail
    }

    private tailrec suspend fun execute(
        record: ProducerRecord<String, String>,
        retryInfo: RetryInfo
    ): Result<RecordMetadata, Fail.Incident.Bus.Producer> =
        when (val result = producer.trySend(record)) {
            is Result.Success -> result
            is Result.Failure -> {
                result.error.logging(logger = logger)

                if (retryInfo.attempts.nonNext)
                    result
                else {
                    delay(retryInfo.delayTime.current)
                    execute(record = record, retryInfo = retryInfo.next())
                }
            }
        }
}
