import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import java.lang.Math.random

sealed class ActorMsg
class Register(val response: CompletableDeferred<Player>) : ActorMsg()
class GetFreeTarget(val playerId: Int, val response: CompletableDeferred<Int?>) : ActorMsg()

class Player(var id: Int? = null, var targetId: Int? = null)

fun CoroutineScope.counterActor() = actor<ActorMsg> {
    val players = mutableListOf<Player>() // actor state
    fun getFreeTargetId(playerId: Int): Int? {
        val busyTargets: List<Int> = players.filter { it.targetId != null }.map { it.targetId!! }
        var i = 0
        while (i in busyTargets || i == playerId)
            i++
        if (i in players.map { it.id })
            return i
        return null
    }
    for (msg in channel) {
        when (msg) {
            is Register -> {
                val player = Player()
                players.add(player)
                player.id = players.indexOf(player)
                player.targetId = getFreeTargetId(player.id!!)
                msg.response.complete(player)
            }
            is GetFreeTarget -> msg.response.complete(getFreeTargetId(msg.playerId))
        }
    }
}

fun main() {
    runBlocking {
        val counter = counterActor() // create the actor
        withContext(Dispatchers.Default) {
            coroutineScope {
                repeat(15) {
                    launch {
                        // Player coroutine
                        delay((random() * 1000).toLong())
                        val response = CompletableDeferred<Player>()
                        counter.send(Register(response)) // Register
                        val player = response.await() // Waiting registration
                        while (player.targetId == null) { // Setting target
                            delay((random() * 1000).toLong())
                            val response = CompletableDeferred<Int?>()
                            counter.send(GetFreeTarget(player.id!!, response))
                            player.targetId = response.await()
                        }
                        println("Id = ${player.id} Target = ${player.targetId}")
                    }
                }
            }
            counter.close() // shutdown the actor
        }
    }
}