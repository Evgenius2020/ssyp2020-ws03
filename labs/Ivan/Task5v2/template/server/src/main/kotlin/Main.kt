import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.*
import kotlin.random.Random

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
@KtorExperimentalAPI
fun main() {
    val server = Server()
    server.start()

    runBlocking {
        launch {
            server.run(this)
        }
        repeat(5) {
            launch {
                delay((5000 * Random.nextDouble()).toLong())
                BotClient(server.serverActor).start()
            }
        }
    }
}