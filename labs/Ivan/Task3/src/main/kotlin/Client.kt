import engine.Player
import engine.Vector
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import java.lang.Math.random

class Client(val server: SendChannel<ServerMsg>) {
    var pId: Long = 0

    lateinit var me: Player
    suspend fun start() {

        println("Starting...")
        val responsePlayer = CompletableDeferred<Player>()
        server.send(Register(responsePlayer))
        println("Sent")
        pId = responsePlayer.await().id
        println("Done")

        while (true) {
            delay(1000)
            val response = CompletableDeferred<HashMap<Long, Player>>()
            server.send(GetPlayers(response))
            val players = response.await()
            me = players[pId]!!
            val target = players[me.targetId]
            val newDir = computeDir(target) ?: Vector(random(), random())
            server.send(ChangeDirection(pId, newDir))
        }
    }

    private fun computeDir(target: Player?): Vector? {
        if (target == null) return null
        return me.pos.directionTo(target.pos)
    }
}