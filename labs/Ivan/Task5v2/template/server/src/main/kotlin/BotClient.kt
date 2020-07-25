import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlin.random.Random

class BotClient(val server: SendChannel<ServerMsg>) {
    var pId: Long = 0

    lateinit var me: Player
    suspend fun start() {

        val responsePlayer = CompletableDeferred<Player>()
        server.send(Register(responsePlayer))
        pId = responsePlayer.await().id

        while (true) {
            delay(300)
            val response = CompletableDeferred<HashMap<Long, Player>>()
            server.send(GetPlayers(response))
            val players = response.await()
            me = players[pId]!!
            val target = players[me.targetId]
            server.send(ChangeDirection(pId, target?.pos ?: Dot(Random.nextDouble(640.0), Random.nextDouble(480.0))))
        }
    }

    private fun computeDir(target: Player?): Vector? {
        if (target == null) return null
        return me.pos.directionTo(target.pos)
    }
}