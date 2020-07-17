import kotlinx.coroutines.channels.*
import kotlinx.coroutines.*
import kotlin.reflect.typeOf

sealed class ServerMsg
class Register(val response : CompletableDeferred<Player>) : ServerMsg()
class GetNewTarget (val playerId : Int, val response: CompletableDeferred<Int?>) : ServerMsg()
class SetAngle (val playerId : Int, val newAngle : Double) : ServerMsg()
class GetPositionById (val playerId: Int, val response: CompletableDeferred<Pair<Double, Double>?>) : ServerMsg()
class Update() : ServerMsg()

@ObsoleteCoroutinesApi
fun CoroutineScope.serverActor() = actor<ServerMsg> {
    for (msg in channel)
    {
        when (msg)
        {
            is Register -> Server.registerPlayer(msg.response)
            is GetNewTarget -> Server.getNewTarget(msg.playerId, msg.response)
            is SetAngle -> Server.setAngle(msg.playerId, msg.newAngle)
            is GetPositionById -> Server.getPositionById(msg.playerId, msg.response)
            is Update -> Server.update()
        }
    }
}

object Server
{
    private val engine = Engine()

    suspend fun update()
    {
        engine.tick()
        delay(100)
    }

    fun getPositionById(playerId: Int, response: CompletableDeferred<Pair<Double, Double>?>)
    {
        if (playerId in engine.playerMap.keys)
        {
            val position = engine.getPositions(playerId)
            //println("Player ($playerId) position is: {$position}")
            response.complete(position)
        }
        else
        {
            response.complete(null)
        }
    }

    fun setAngle(playerId : Int, angle : Double)
    {
        engine.setAngle(playerId, angle)
    }

    fun getNewTarget(playerId : Int, response: CompletableDeferred<Int?>)
    {
        if (playerId in engine.playerMap.keys)
        {
            val newTarget = engine.getNewTarget(playerId)
            engine.playerMap[playerId]?.setTarget(newTarget)
            response.complete(newTarget)
        }
        else
            response.complete(null)
    }

    fun registerPlayer(response: CompletableDeferred<Player>)
    {
        response.complete(engine.registerPlayer())
    }

}