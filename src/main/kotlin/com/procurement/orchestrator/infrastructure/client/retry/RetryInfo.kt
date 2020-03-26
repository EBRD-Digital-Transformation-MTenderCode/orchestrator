package com.procurement.orchestrator.infrastructure.client.retry

class RetryInfo(val delayTime: DelayTime = DelayTime(), val attempts: Attempts = Attempts.Limited()) {

    fun next(): RetryInfo = RetryInfo(delayTime = delayTime.next(), attempts = attempts.next())
}
