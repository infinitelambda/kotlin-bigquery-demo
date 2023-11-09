package com.infinitelambda.application.route

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.effect
import arrow.core.raise.fold
import com.infinitelambda.application.FormResultError
import com.infinitelambda.application.FormResultErrors
import com.infinitelambda.application.FormResultNotPersisted
import com.infinitelambda.application.InvalidFormResult
import com.infinitelambda.application.bigQuery.BigQueryWriteStream
import com.infinitelambda.application.data.FormResult
import com.infinitelambda.application.service.FormResultService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*

context(BigQueryWriteStream)
fun Routing.formResultRoutes() {

    route("/formResults") {
        post {
            conduit(HttpStatusCode.NoContent) {
                val result = receiveCatching()
                FormResultService.save(result)
            }
        }
    }

}

typealias KtorCtx = PipelineContext<Unit, ApplicationCall>

context(KtorCtx)
private suspend inline fun <reified A : Any> conduit(
    status: HttpStatusCode,
    crossinline block: suspend context(FormResultErrors) () -> A
): Unit = effect { block(this) }
    .fold({ respond(it) }, { call.respond(status, it) })

private suspend fun KtorCtx.respond(error: FormResultError) =
    when (error) {
        is FormResultNotPersisted -> call.respond(
            HttpStatusCode.InternalServerError,
            "Unexpected failure: ${error.message}, caused by ${error.cause}"
        )

        is InvalidFormResult -> call.respond(HttpStatusCode.UnprocessableEntity, error.message)
    }

context(Raise<InvalidFormResult>)
private suspend inline fun KtorCtx.receiveCatching(): FormResult =
    Either.catch { call.receive<FormResult>() }
        .mapLeft { InvalidFormResult(it.message ?: "Could not parse form result") }
        .bind()