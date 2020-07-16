import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking


@KtorExperimentalAPI
fun main(args: Array<String>) {
    val srv = Server()
    srv.run()
}
