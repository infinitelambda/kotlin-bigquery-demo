package com.infinitelambda.application.route

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.effect
import arrow.core.raise.fold
import com.google.cloud.language.v2.LanguageServiceClient
import com.infinitelambda.application.*
import com.infinitelambda.application.bigQuery.BigQueryWriteStream
import com.infinitelambda.application.data.FormResult
import com.infinitelambda.application.service.FormResultService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*

context(BigQueryWriteStream, LanguageServiceClient)
fun Routing.formResultRoutes() {

    route("/formResults") {
        post {
            call.application.environment.log.info("Request to /formResults")

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

        is EnrichmentFailed -> call.respond(
            HttpStatusCode.InternalServerError,
            "Error while enriching data: ${error.message}"
        )

        is InvalidFormResult -> call.respond(HttpStatusCode.UnprocessableEntity, error.message)
    }

context(Raise<InvalidFormResult>)
private suspend inline fun KtorCtx.receiveCatching(): FormResult =
    Either.catch { call.receive<FormResult>() }
        .mapLeft { InvalidFormResult(it.message ?: "Could not parse form result") }
        .bind()