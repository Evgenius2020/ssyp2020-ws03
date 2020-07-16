import Engine.Player
import Engine.Vector
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import java.lang.Math.random

class Client(val server: SendChannel<ServerMsg>) {
    var pId: Long = 0

    var me: Player? = null
    var target: Player? = null

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
            me = players[pId]
            if(me == null) {
                break
            }
            target = players[me!!.targetId]
            val newDir = computeDir() ?: Vector(random(), random())
            server.send(ChangeDirection(pId, newDir))
        }
    }

    private fun computeDir(): Vector? {
        if(target == null) return null
        return Vector(me!!.pos, target!!.pos)
    }
}