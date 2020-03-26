package com.procurement.orchestrator.infrastructure.extension.kafka

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend inline fun <reified K : Any, reified V : Any> KafkaProducer<K, V>.trySend(record: ProducerRecord<K, V>): Result<RecordMetadata, Fail.Incident.Bus.Producer> =
    suspendCoroutine { continuation ->
        val callback = Callback { metadata, exception ->
            if (exception != null)
                continuation.resume(failure(Fail.Incident.Bus.Producer(description = "", exception = exception)))
            else
                continuation.resume(success(metadata))
        }
        this.send(record, callback)
    }
