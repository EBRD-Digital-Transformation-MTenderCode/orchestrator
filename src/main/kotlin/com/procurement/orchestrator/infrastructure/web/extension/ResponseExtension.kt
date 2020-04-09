package com.procurement.orchestrator.infrastructure.web.extension

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

fun MaybeFail<Fail>.buildResponse(): ResponseEntity<String> = when (this) {
    is MaybeFail.None -> ResponseEntity(HttpStatus.ACCEPTED)
    is MaybeFail.Fail -> {
        when (error) {
            is Fail.Error -> {
                val body = buildErrorBody(code = error.code, description = error.description)
                ResponseEntity<String>(body, HttpStatus.BAD_REQUEST)
            }
            is Fail.Incident -> ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}

private fun buildErrorBody(code: String, description: String) = """{"code":"$code","description":"$description"}"""
