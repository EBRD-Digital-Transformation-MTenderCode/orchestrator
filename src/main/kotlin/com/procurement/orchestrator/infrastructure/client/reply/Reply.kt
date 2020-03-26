package com.procurement.orchestrator.infrastructure.client.reply

import com.procurement.orchestrator.domain.EnumElementProvider

class Reply<out T>(val id: ReplyId, val version: String, val status: Status, val result: Result<T>) {

    companion object {
        val None: Reply<Unit>
            get() = Reply(
                id = EMPTY_REPLY_ID,
                version = "",
                status = Status.SUCCESS,
                result = Result.Success(Unit)
            )
    }

    fun apply(function: T.() -> Unit): Reply<T> {
        result.apply(function)
        return this
    }

    fun also(function: (T) -> Unit): Reply<T> {
        result.also(function)
        return this
    }

    sealed class Result<out T> {

        inline fun apply(function: T.() -> Unit): Result<T> {
            when (this) {
                is Success -> value.function()
                is Errors -> Unit
                is Incident -> Unit
            }
            return this
        }

        inline fun also(function: (T) -> Unit): Result<T> {
            when (this) {
                is Success -> function(value)
                is Errors -> Unit
                is Incident -> Unit
            }
            return this
        }

        class Success<T>(val value: T) : Result<T>()

        class Errors(values: List<Error>) : List<Errors.Error> by values, Result<Nothing>() {

            class Error(val code: String, val description: String, val details: List<Detail>) {
                class Detail(val name: String)
            }
        }

        class Incident(
            val id: String,
            val date: String,
            val level: Level,
            val service: Service,
            val details: List<Detail>
        ) : Result<Nothing>() {

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
