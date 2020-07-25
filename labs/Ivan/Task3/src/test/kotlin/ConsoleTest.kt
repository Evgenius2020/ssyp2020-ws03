import engine.Player
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel

class ClientServerTester {

    private suspend fun allClients(server: SendChannel<ServerMsg>){
        val request = CompletableDeferred<HashMap<Long, Player>>()
        server.send(GetPlayers(request))
        for (i in request.await()) {
            println("${i.component1()} ${i.component2()}")
        }
        println("Done")
    }

    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    fun runConsoleAsClient() {
        runBlocking {
            val server = startServer(this)
            withContext(Dispatchers.Default) {
                coroutineScope {
                    while (true) {
                        val s = readLine()
                        when (s) {
                            "update" -> {
                                server.send(Update)
                                allClients(server)
                            }
                            "make client" -> {
                                launch {
                                    Client(server).start()
                                }
                            }
                            "all clients" -> allClients(server)
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
fun main() {
    ClientServerTester().runConsoleAsClient()
}