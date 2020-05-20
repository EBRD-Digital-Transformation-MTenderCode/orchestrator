package com.procurement.orchestrator.infrastructure.extension.cassandra

import com.datastax.driver.core.BatchStatement
import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Session
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success

fun BoundStatement.tryExecute(session: Session): Result<ResultSet, Fail.Incident.Database.Access> = try {
    success(session.execute(this))
} catch (expected: Exception) {
    failure(Fail.Incident.Database.Access(description = "Error accessing to database.", exception = expected))
}

fun BatchStatement.tryExecute(session: Session): Result<ResultSet, Fail.Incident.Database.Access> = try {
    success(session.execute(this))
} catch (expected: Exception) {
    failure(Fail.Incident.Database.Access(description = "Error accessing to database.", exception = expected))
}
