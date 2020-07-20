package server

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import shared.*

sealed class EngineActorMsg
class Register (val responseTask: CompletableDeferred<Player>) : EngineActorMsg()
class Remove (val playerId: Int) : EngineActorMsg()
class GetMap (val responseTask: CompletableDeferred<MutableMap<Int, Player>>) : EngineActorMsg()
class GetNewTarget (val playerId : Int, val responseTask: CompletableDeferred<Int?>) : EngineActorMsg()
class SetAngle (val playerId: Int, val newAngle : Double) : EngineActorMsg()
object Update : EngineActorMsg()

class GetPlayers (val responseTask: CompletableDeferred<List<Player>>) : EngineActorMsg()

@ObsoleteCoroutinesApi
fun CoroutineScope.engineActor() = actor<EngineActorMsg> {
    val engine = Engine()

    for (msg in channel) {
        when (msg) {
            is Register -> msg.responseTask.complete(engine.registerPlayer())
            is Remove -> engine.removePlayer(msg.playerId)
            is GetMap -> msg.responseTask.complete(engine.playerMap)
            is GetNewTarget -> {
                if (msg.playerId in engine.playerMap.keys)
                {
                    val newTarget = engine.getNewTarget(msg.playerId)
                    engine.playerMap[msg.playerId]!!.setTarget(newTarget)
                    msg.responseTask.complete(newTarget)
                }
                else
                {
                    msg.responseTask.complete(null)
                }
            }
            is SetAngle -> engine.setAngle(msg.playerId, msg.newAngle)
            Update -> engine.tick()
        }
    }
}