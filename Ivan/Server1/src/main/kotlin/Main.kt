import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.*

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
@KtorExperimentalAPI
fun main() {
    val server = Server()
    server.start()

    runBlocking {
        server.run(this)
    }
}