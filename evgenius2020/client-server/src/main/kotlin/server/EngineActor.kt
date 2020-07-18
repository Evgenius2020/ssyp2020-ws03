package server

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import shared.Player

sealed class EngineActorMsg
class Register(val responseTask: CompletableDeferred<Player>) : EngineActorMsg()
class Remove(val player: Player) : EngineActorMsg()
object Tick : EngineActorMsg()
class GetPlayers(val responseTask: CompletableDeferred<List<Player>>) : EngineActorMsg()

fun CoroutineScope.engineActor() = actor<EngineActorMsg> {
    val engine = Engine()

    for (msg in channel) {
        when (msg) {
            is Register -> msg.responseTask.complete(engine.register())
            is Remove -> engine.remove(msg.player)
            Tick -> engine.tick()
            is GetPlayers -> msg.responseTask.complete(engine.getPlayers())
        }
    }
}
