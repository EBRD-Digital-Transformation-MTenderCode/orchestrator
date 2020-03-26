package com.procurement.orchestrator.infrastructure.client.reply

import java.util.*

typealias ReplyId = UUID

val EMPTY_REPLY_ID: ReplyId = ReplyId.fromString("00000000-0000-0000-0000-000000000000")

fun String.replyId(): ReplyId? = try {
    UUID.fromString(this)
} catch (expected: Exception) {
    null
}

operator fun ReplyId.invoke(value: String): ReplyId? = try {
    UUID.fromString(value)
} catch (expected: Exception) {
    null
}
