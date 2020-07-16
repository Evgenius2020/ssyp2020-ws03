import Client
import Server
import Engine
import kotlinx.coroutines.*
import java.lang.Math.random
import java.util.concurrent.ThreadLocalRandom

fun main()
{
    runBlocking {
        Server.tick()
        withContext(Dispatchers.Default) {
            coroutineScope {
                repeat(15) {
                    launch {
                        delay((random() * 1000).toLong())
                        var client = Client()
                        client.start()
                        println("Client added")
                    }
                }
            }
        }
    }
}