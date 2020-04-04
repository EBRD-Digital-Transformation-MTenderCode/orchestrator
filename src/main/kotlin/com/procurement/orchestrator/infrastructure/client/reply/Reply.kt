package com.procurement.orchestrator.infrastructure.client.reply

import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.infrastructure.model.Version

sealed class Reply<out T> {

    inline fun apply(function: Option<T>.() -> Unit): Reply<T> {
        when (this) {
            is Success -> result.function()
            else -> Unit
        }
        return this
    }

    inline fun also(function: (Option<T>) -> Unit): Reply<T> {
        when (this) {
            is Success -> function(result)
            else -> Unit
        }
        return this
    }

    object None : Reply<Nothing>()

    class Success<T>(val id: ReplyId, val version: Version, val result: Option<T>) : Reply<T>()

    class Errors(val id: ReplyId, val version: Version, val result: Result) : Reply<Nothing>() {

        class Result(values: List<Error>) : List<Result.Error> by values {

            class Error(val code: String, val description: String, val details: List<Detail>) {

                class Detail(val id: String?, val name: String?)
            }
        }
    }

    class Incident(val id: ReplyId, val version: Version, val result: Result) : Reply<Nothing>() {

        class Result(
            val id: String,
            val date: String,
            val level: Level,
            val service: Service,
            val details: List<Detail>
        ) {

            data class Service(val id: String, val name: String, val version: String)

            data class Detail(val code: String, val description: String, val metadata: String)

            enum class Level(override val key: String) : EnumElementProvider.Key {
                ERROR("error"),
                WARNING("warning"),
                INFO("info");

                companion object : EnumElementProvider<Level>(info = info())
            }
        }
    }

    enum class Status(override val key: String) : EnumElementProvider.Key {
        SUCCESS("success"),
        ERROR("error"),
        INCIDENT("incident");

        companion object : EnumElementProvider<Status>(info = info())
    }
}
