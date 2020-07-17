import com.soywiz.korge.Korge
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlin.collections.MutableMap
import kotlin.collections.contains
import kotlin.collections.mutableMapOf
import kotlin.collections.set

class GUI (private val serverActor : SendChannel<ServerMsg>)
{
    private val radius = 10.0
    suspend fun start()
    {
        Korge(width = 640, height = 640, bgcolor = Colors["#2b2b2b"])
        {
            val graphicsMap = mutableMapOf<Int, Pair<Circle, Text>>()
            val responseMap = CompletableDeferred<MutableMap<Int, Player>>()
            serverActor.send(GetMap(responseMap))
            val map = responseMap.await()

            for (i in map.keys)
            {
                val circle = circle (radius, Colors.ORANGE).xy(map[i]!!.getX() - radius, map[i]!!.getY() - radius)
                val text = text ("${map[i]!!.getId()}", 18.0).xy(map[i]!!.getX() - radius + 1.0, map[i]!!.getY() - radius + 1.0)
                graphicsMap[i] = Pair(circle, text)
            }

            while (true)
            {
                val responseMapUpdate = CompletableDeferred<MutableMap<Int, Player>>()
                serverActor.send(GetMap(responseMapUpdate))
                val mapUpdate = responseMap.await()

                for (i in map.keys)
                {
                    if (i in graphicsMap)
                    {
                        graphicsMap[i]!!.first.xy(map[i]!!.getX() - radius, map[i]!!.getY() - radius)
                        graphicsMap[i]!!.second.xy(map[i]!!.getX() - radius + 1.0, map[i]!!.getY() - radius + 1.0)
                    }
                    else
                    {
                        val circle = circle (radius, Colors.ORANGE).xy(map[i]!!.getX() - radius, map[i]!!.getY() - radius)
                        val text = text ("${map[i]!!.getId()}", 18.0).xy(map[i]!!.getX() - radius + 1.0, map[i]!!.getY() - radius + 1.0)
                        graphicsMap[i] = Pair(circle, text)
                    }
                }
            }
        }
    }
}