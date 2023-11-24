import com.infinitelambda.application.DashboardController
import com.infinitelambda.application.FeedbackFromController
import com.infinitelambda.application.TabController
import com.infinitelambda.application.data.*
import com.infinitelambda.application.html.*
import google.visualization.DataTable
import google.visualization.PieChart
import google.visualization.PieChartOptions
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import mdc.tabBar.MDCTabBar
import org.w3c.dom.Element
import org.w3c.dom.HTMLFormElement
import org.w3c.dom.get
import org.w3c.xhr.FormData
import kotlin.time.Duration.Companion.seconds

private val client = HttpClient(Js) {
    install(ContentNegotiation) {
        json()
    }

    install(WebSockets) {
        pingInterval = 15.seconds.inWholeMilliseconds
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }
}

val mainScope = MainScope()

fun main() {
    val form = document.getElementById(formId) as HTMLFormElement
    val dashboard = document.getElementById(dashboardId)
    TabController(document.querySelector(".mdc-tab-bar"), listOfNotNull(form, dashboard))

    FeedbackFromController(form, client, mainScope)
    DashboardController(client, mainScope)
}
