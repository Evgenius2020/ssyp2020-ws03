import com.soywiz.korge.Korge
import com.soywiz.korge.internal.KorgeInternal
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlin.collections.MutableMap
import kotlin.collections.contains
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.math.atan2

class GUI (private val serverActor : SendChannel<ServerMsg>)
{
    private var playerId : Int = -1
    private var targetId : Int? = null
    private val radius = Config.radius

    @KorgeInternal
    suspend fun start()
    {

        val responseRegister = CompletableDeferred<Player>()
        serverActor.send(Register(responseRegister))
        val answer = responseRegister.await()
        playerId = answer.getId()!!
        targetId = answer.getTargetId()
        if (playerId != -1) {
            println()
            Korge(width = Config.maxX, height = Config.maxY, bgcolor = Colors["#2b2b2b"], title = "Player's id is: {$playerId}")
            {
                val graphicsMap = mutableMapOf<Int, Pair<Circle, Text>>()
                val responseMap = CompletableDeferred<MutableMap<Int, Player>>()
                serverActor.send(GetMap(responseMap))
                val map = responseMap.await()

                for (i in map.keys) {
                    if (i == playerId)
                    {
                        val circle = circle(radius, Colors.PURPLE).xy(map[i]!!.getX() - radius, map[i]!!.getY() - radius)
                        val text = text("${map[i]!!.getId()}", 18.0).xy(map[i]!!.getX() - radius + 1.0, map[i]!!.getY() - radius + 1.0)
                        graphicsMap[i] = Pair(circle, text)
                    }
                    else
                    {
                        val circle = circle(radius, Colors.ORANGE).xy(map[i]!!.getX() - radius, map[i]!!.getY() - radius)
                        val text = text("${map[i]!!.getId()}", 18.0).xy(map[i]!!.getX() - radius + 1.0, map[i]!!.getY() - radius + 1.0)
                        graphicsMap[i] = Pair(circle, text)
                    }
                }

                while (true) {
                    delay(Config.ping)
                    val responseMapUpdate = CompletableDeferred<MutableMap<Int, Player>>()
                    serverActor.send(GetMap(responseMapUpdate))
                    val mapUpdate = responseMap.await()

                    targetId = mapUpdate[playerId]!!.getTargetId()

                    if (targetId == null)
                    {
                        delay(Config.ping)
                        val responseTarget = CompletableDeferred<Int?>()
                        serverActor.send(GetNewTarget(playerId, responseTarget))
                        targetId = responseTarget.await()
                    }

                    for (i in mapUpdate.keys) {
                        if (i in graphicsMap) {
                            graphicsMap[i]!!.first.xy(mapUpdate[i]!!.getX() - radius, mapUpdate[i]!!.getY() - radius)
                            graphicsMap[i]!!.second.xy(mapUpdate[i]!!.getX() - radius + 1.0, mapUpdate[i]!!.getY() - radius + 1.0)
                        }
                        else
                        {
                            val circle = circle(radius, Colors.ORANGE).xy(mapUpdate[i]!!.getX() - radius, mapUpdate[i]!!.getY() - radius)
                            val text = text("${mapUpdate[i]!!.getId()}", 18.0).xy(mapUpdate[i]!!.getX() - radius + 1.0, mapUpdate[i]!!.getY() - radius + 1.0)
                            graphicsMap[i] = Pair(circle, text)
                        }
                        if (i == targetId)
                            graphicsMap[i]!!.first.color = Colors.RED
                        else
                            if (i != playerId)
                                graphicsMap[i]!!.first.color = Colors.ORANGE
                    }

                    val mX = views.nativeMouseX
                    val mY = views.nativeMouseY
                    val pX = mapUpdate[playerId]!!.getX()
                    val pY = mapUpdate[playerId]!!.getY()
                    val angle = atan2(mY - pY, mX - pX)
                    serverActor.send(SetAngle(playerId, angle))
                }
            }
        }
        else
        {
            println("Registration failed")
        }
    }
}