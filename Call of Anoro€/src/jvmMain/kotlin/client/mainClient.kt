package client

import com.soywiz.klock.seconds
import com.soywiz.korev.mouse
import com.soywiz.korge.*
import com.soywiz.korge.input.onClick
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
import io.ktor.util.KtorExperimentalAPI
import io.ktor.utils.io.readUTF8Line
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import shared.*
import java.net.InetSocketAddress

@KtorExperimentalAPI
fun main() {
    runBlocking {
        val socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("127.0.0.1", 1221))
        val input = socket.openReadChannel()
        val output = socket.openWriteChannel(autoFlush = true)
        var pepe = 1

        val size = ClientConfiguration.size

        Korge(width = 640, height = 640, bgcolor = Colors["#2B2B2B"], title = "Call of Anoro€++ redux")
        {

            stage.onClick {
                output.writeStringUtf8(serialize(Shoot) + '\n')
                println("Shot number $pepe")
                pepe++
            }

            val graphicsMap = mutableMapOf<Int, SolidRect>()
            output.writeStringUtf8(serialize(GetRenderInfo) + '\n')
            val initResponse = input.readUTF8Line()!!
            val initMap = deserialize(initResponse) as RenderInfo

            for (i in initMap.entities) {
                val square = solidRect(size, size, Colors.PURPLE).anchor(0.5, 0.5).xy(i.x, i.y).rotation(Angle(i.angle))
                graphicsMap[i.id] = square
            }
            while (true) {
                output.writeStringUtf8(serialize(GetRenderInfo) + '\n')
                val response = input.readUTF8Line()!!
                val map = deserialize(response) as RenderInfo

                val exist = mutableListOf<Int>()

                for (i in map.entities)
                    exist.add(i.id)

                val iterator = graphicsMap.iterator()
                while (iterator.hasNext()) {
                    val item = iterator.next()
                    if (item.key !in exist) {
                        removeChild(item.value)
                        iterator.remove()
                    }
                }

                for (i in map.entities) {
                    if (i.id in graphicsMap) {
                        graphicsMap[i.id]!!.xy(i.x, i.y).rotation(Angle(i.angle))
                    } else {
                        val square = solidRect(size, size, Colors.PURPLE).anchor(0.5, 0.5).xy(i.x, i.y).rotation(Angle(i.angle))
                        graphicsMap[i.id] = square
                    }
                }

                val mX = views.nativeMouseX
                val mY = views.nativeMouseY

                output.writeStringUtf8(serialize(SetAngle(ClientServerPoint(mX, mY))) + '\n')
            }
        }
    }
}