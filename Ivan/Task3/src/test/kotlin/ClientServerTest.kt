import Engine.Player
import Engine.Vector
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel

class ConsoleClient(val server: SendChannel<ServerMsg>) {
    val pId: Long = 0

    var me: Player? = null
    var target: Player? = null

    suspend fun act() {
        val response = CompletableDeferred<HashMap<Long, Player>>()
        server.send(GetPlayers(response))
        val players = response.await()
        me = players[pId]
        if (me == null) {
            return
        }
        target = players[me!!.targetId]
        val newDir = computeDir() ?: Vector(Math.random(), Math.random())
        server.send(ChangeDirection(pId, newDir))
    }

    private fun computeDir(): Vector? {
        if (target == null) return null
        return Vector(me!!.pos, target!!.pos)
    }

}

class ClientServerTester {

    suspend fun allClients(server: SendChannel<ServerMsg>){
        val request = CompletableDeferred<HashMap<Long, Player>>()
        server.send(GetPlayers(request))
        for (i in request.await()) {
            println("${i.component1()} ${i.component2()}")
        }
        println("Done")
    }

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

fun main() {
    ClientServerTester().runConsoleAsClient()
}