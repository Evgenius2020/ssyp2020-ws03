import Engine.Player
import Engine.Vector
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import java.lang.Math.random


@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
fun main() {
    runBlocking {
        val server = startServer(this)
        withContext(Dispatchers.Default) {
            coroutineScope {
                repeat(20) {
                    launch {
                        delay((1000 * random()).toLong())
                        val c = Client(server)
                        c.start()
                    }
                }
            }
            server.close()
        }
    }
}