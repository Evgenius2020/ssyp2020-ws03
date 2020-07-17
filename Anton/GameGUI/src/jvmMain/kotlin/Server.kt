import kotlinx.coroutines.channels.*
import kotlinx.coroutines.*
import kotlin.reflect.typeOf

sealed class ServerMsg
class Register(val response : CompletableDeferred<Player>) : ServerMsg()
class GetNewTarget (val playerId : Int, val response: CompletableDeferred<Int?>) : ServerMsg()
class SetAngle (val playerId : Int, val newAngle : Double) : ServerMsg()
class GetMap (val response: CompletableDeferred<MutableMap<Int, Player>>) : ServerMsg()
object Update : ServerMsg()

@ObsoleteCoroutinesApi
fun CoroutineScope.serverActor() = actor<ServerMsg> {
    val server = Server()
    for (msg in channel)
    {
        when (msg)
        {
            is Register -> server.registerPlayer(msg.response)
            is GetNewTarget -> server.getNewTarget(msg.playerId, msg.response)
            is SetAngle -> server.setAngle(msg.playerId, msg.newAngle)
            is GetMap -> server.getMap(msg.response)
            is Update -> server.update()
        }
    }
}

class Server
{
    private val engine = Engine()

    suspend fun update()
    {
        engine.tick()
    }

    fun getMap(response: CompletableDeferred<MutableMap<Int, Player>>)
    {
        response.complete(engine.playerMap)
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
            engine.playerMap[playerId]!!.setTarget(newTarget)
            response.complete(newTarget)
        }
        else
        {
            response.complete(null)
        }
    }

    fun registerPlayer(response: CompletableDeferred<Player>)
    {
        response.complete(engine.registerPlayer())
    }

}