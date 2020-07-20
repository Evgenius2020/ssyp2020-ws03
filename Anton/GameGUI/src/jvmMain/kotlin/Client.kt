import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.channels.*
import kotlin.math.atan2

class Client (private val serverActor : SendChannel<ServerMsg>)
{
    private var playerId : Int = -1
    private var targetId : Int? = null
    suspend fun start()
    {
        val responseRegister = CompletableDeferred<Player>()
        serverActor.send(Register(responseRegister))
        val answer = responseRegister.await()
        playerId = answer.getId()!!
        targetId = answer.getTargetId()
        if (playerId != -1) {
            while (true) {
                val responseMap = CompletableDeferred<MutableMap<Int, Player>>()
                serverActor.send(GetMap(responseMap))
                val map = responseMap.await()
                targetId = map[playerId]!!.getTargetId()

                //println("$playerId target is $targetId")

                if (targetId != null)
                {
                    delay(Config.ping)
                    val targetPos = Pair(map[targetId!!]!!.getX(), map[targetId!!]!!.getY())
                    val playerPos = Pair(map[playerId]!!.getX(), map[playerId]!!.getY())

                    val angle = atan2(targetPos.second - playerPos.second, targetPos.first - playerPos.first)

                    serverActor.send(SetAngle(playerId, angle))

                }
                else
                {
                    val responseTarget = CompletableDeferred<Int?>()
                    serverActor.send(GetNewTarget(playerId, responseTarget))
                    targetId = responseTarget.await()
                }
                delay(Config.ping)
            }
        }
        else
        {
            println("Registration failed")
        }
    }
}