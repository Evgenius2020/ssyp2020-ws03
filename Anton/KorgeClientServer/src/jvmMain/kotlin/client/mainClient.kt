package client

import com.soywiz.korge.Korge
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.*
import io.ktor.util.KtorExperimentalAPI
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import server.GetMapRequest
import server.GetNewTargetRequest
import server.SetAngleRequest
import shared.Player
import shared.deserialize
import shared.serialize
import java.net.InetSocketAddress
import shared.*
import com.soywiz.klock.seconds
import com.soywiz.korge.*
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.degrees
import com.soywiz.korma.interpolation.Easing
import kotlin.math.atan2

@KtorExperimentalAPI
fun main()
{
    runBlocking {
        val socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("127.0.0.1", 2323))
        val input = socket.openReadChannel()
        val output = socket.openWriteChannel(autoFlush = true)
        val radius = Config.radius
        val player = deserialize(input.readUTF8Line()!!) as Player
        var targetId = player.getTargetId()
        println("Registered: $player")

        //while (true) {
            Korge (width = Config.maxX, height = Config.maxY, bgcolor = Colors["#2b2b2b"], title = "Player's id is: {${player.getId()}}")
            {
                val graphicsMap = mutableMapOf<Int, Pair<Circle, Text>>()
                val mapInitRequest = GetMapRequest()
                output.writeStringUtf8(serialize(mapInitRequest) + '\n')
                val initResponse = input.readUTF8Line()!!
                val initMap = deserialize(initResponse) as MutableMap<Int, Player>

                for (i in initMap.keys)
                {
                    if (i == player.getId())
                    {
                        val circle = circle(radius, Colors.PURPLE).xy(initMap[i]!!.getX() - radius, initMap[i]!!.getY() - radius)
                        val text = text("${initMap[i]!!.getId()}", 18.0).xy(initMap[i]!!.getX() - radius/2, initMap[i]!!.getY() - radius/2)
                        graphicsMap[i] = Pair(circle, text)
                    }
                    else
                    {
                        val circle = circle(radius, Colors.ORANGE).xy(initMap[i]!!.getX() - radius, initMap[i]!!.getY() - radius)
                        val text = text("${initMap[i]!!.getId()}", 18.0).xy(initMap[i]!!.getX() - radius/2, initMap[i]!!.getY() - radius/2)
                        graphicsMap[i] = Pair(circle, text)
                    }
                }

                while (true)
                {
                    val mapRequest = GetMapRequest()
                    output.writeStringUtf8(serialize(mapRequest) + '\n')
                    val response = input.readUTF8Line()!!
                    val map = deserialize(response) as MutableMap<Int, Player>

                    targetId = map[player.getId()]!!.getTargetId()

                    if (targetId == null)
                    {
                        //delay(Config.updateTime) // TODO: epic kostil'

                        val getTargetRequest = GetNewTargetRequest(player.getId()!!)
                        output.writeStringUtf8(serialize(getTargetRequest) + '\n')
                        val targetResponse = input.readUTF8Line()!!
                        val newTarget = deserialize(targetResponse) as Int?
                        targetId = newTarget
                    }

                    for (i in map.keys) {
                        if (i in graphicsMap) {
                            graphicsMap[i]!!.first.xy(map[i]!!.getX() - radius, map[i]!!.getY() - radius)
                            graphicsMap[i]!!.second.xy(map[i]!!.getX() - radius + 1.0, map[i]!!.getY() - radius + 1.0)
                        }
                        else
                        {
                            val circle = circle(radius, Colors.ORANGE).xy(map[i]!!.getX() - radius, map[i]!!.getY() - radius)
                            val text = text("${map[i]!!.getId()}", 18.0).xy(map[i]!!.getX() - radius + 1.0, map[i]!!.getY() - radius + 1.0)
                            graphicsMap[i] = Pair(circle, text)
                        }
                        if (i == targetId)
                            graphicsMap[i]!!.first.color = Colors.RED
                        else
                            if (i != player.getId())
                                graphicsMap[i]!!.first.color = Colors.ORANGE
                    }

                    val mX = views.nativeMouseX
                    val mY = views.nativeMouseY
                    val pX = map[player.getId()]!!.getX()
                    val pY = map[player.getId()]!!.getY()
                    val angle = atan2(mY - pY, mX - pX)

                    delay(Config.ping)

                    val setAngleRequest = SetAngleRequest(player.getId()!!, angle)
                    output.writeStringUtf8(serialize(setAngleRequest) + '\n')
                }
            }
        //}
    }
}