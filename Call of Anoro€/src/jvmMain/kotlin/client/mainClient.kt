package client

import com.soywiz.klock.seconds
import com.soywiz.korev.mouse
import com.soywiz.korge.*
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.degrees
import com.soywiz.korma.interpolation.Easing
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.readUTF8Line
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import shared.ClientServerPoint
import shared.RenderInfo
import shared.deserialize
import shared.serialize
import java.net.InetSocketAddress

fun main()
{
    runBlocking {
        val socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("127.0.0.1", 1221))
        val input = socket.openReadChannel()
        val output = socket.openWriteChannel(autoFlush = true)
        Korge (width = 640, height = 640, bgcolor = Colors["#2b2b2b"], title = "Call of Anoro€++ redux")
        {
            val graphicsMap = mutableMapOf<Int, SolidRect>()
            output.writeStringUtf8(serialize("getRenderInfo") + '\n')
            val initResponse = input.readUTF8Line()!!
            val initMap = deserialize(initResponse) as RenderInfo

            for (i in initMap.entities)
            {
                val square = SolidRect(20.0, 20.0, Colors.PURPLE).xy(i.x - 10.0, i.y - 10.0).rotation(Angle(i.angle))
                graphicsMap[i.id] = square
            }

            while (true)
            {
                output.writeStringUtf8(serialize("getRenderInfo") + '\n')
                val response = input.readUTF8Line()!!
                val map = deserialize(initResponse) as RenderInfo

                val exist = map.entities

                for (i in graphicsMap.keys)
                {
                    if (i !in map.entities.map {it.id})
                    {
                        graphicsMap.remove(i)
                    }
                }

                for (i in map.entities)
                {
                    if (i.id in graphicsMap)
                    {
                        graphicsMap[i.id]!!.xy(i.x, i.y).rotation(Angle(i.angle))
                    }
                    else
                    {
                        val square = SolidRect(20.0, 20.0, Colors.PURPLE).xy(i.x - 10.0, i.y - 10.0).rotation(Angle(i.angle))
                        graphicsMap[i.id] = square
                    }
                }

                val mX = views.nativeMouseX
                val mY = views.nativeMouseY

                output.writeStringUtf8(serialize(ClientServerPoint(mX, mY)) + '\n')

                views.mouse {
                    click {
                        launch {
                            output.writeStringUtf8(serialize("shoot") + '\n')
                        }
                    }
                }
            }
        }
    }
}