package com.infinitelambda.application

import com.infinitelambda.application.data.Comment
import com.infinitelambda.application.data.FavouriteFood
import com.infinitelambda.application.data.FormResult
import com.infinitelambda.application.data.KotlinInterestLevel
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLFormElement
import org.w3c.xhr.FormData

class FeedbackFromController(container: HTMLFormElement, client: HttpClient, scope: CoroutineScope) {

    init {
        container.addEventListener("submit", { event ->
            event.preventDefault()

            val data = FormData(container).toFormResult()

            scope.launch {
                submitForm(client, data)
                container.reset()
                window.alert("Thank you for your submission")
            }
        })
    }

    private suspend fun submitForm(client: HttpClient, data: FormResult) {
        client.post("/formResults") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }
    }


}

private fun FormData.toFormResult() =
    FormResult(
        favouriteFood = FavouriteFood.valueOf(get("favouriteFood") as String),
        kotlinInterestLevel = KotlinInterestLevel.valueOf(get("kotlinInterestLevel") as String),
        comment = Comment(get("comment") as String)
    )