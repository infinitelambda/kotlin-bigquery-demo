import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.infinitelambda.application.App
import com.infinitelambda.application.html.canvasId
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.jetbrains.skiko.wasm.onWasmReady
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

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    onWasmReady {
        CanvasBasedWindow(canvasElementId = canvasId) {
            App(client)
        }
    }
}