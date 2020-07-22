package client

import com.soywiz.klock.seconds
import com.soywiz.korge.Korge
import com.soywiz.korge.input.onClick
import com.soywiz.korge.tiled.TiledMapView
import com.soywiz.korge.tiled.readTiledMap
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.Angle
import engine.Configuration
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.util.KtorExperimentalAPI
import io.ktor.utils.io.readUTF8Line
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import shared.*
import java.net.InetSocketAddress
import kotlin.collections.contains
import kotlin.collections.iterator
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.set

@KtorExperimentalAPI
fun main() {
    runBlocking {
        val socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("127.0.0.1", 1221))
        val input = socket.openReadChannel()
        val output = socket.openWriteChannel(autoFlush = true)
        val playerSize = ClientConfiguration.sizeOfPlayer
        val objectSize = ClientConfiguration.sizeOfObject
        val bulletSize = ClientConfiguration.sizeOfBullet

        Korge(width = 640, height = 640, bgcolor = Colors["#2B2B2B"], title = "Call of Anoro€++ redux")
        {
            val spriteMap = resourcesVfs["BOOM.png"].readBitmap()

            val boomAnimation = SpriteAnimation(
                    spriteMap = spriteMap,
                    spriteWidth = 960 / 5,
                    spriteHeight = 384 / 2,
                    columns = 5,
                    rows = 2
            )

            val mapView = TiledMapView(resourcesVfs["map.tmx"].readTiledMap())
            addChild(mapView)

            views.root.onClick {
                output.writeStringUtf8(serialize(Shoot) + '\n')
                println("SEND SHOOT")
            }

            val graphicsMap = mutableMapOf<Int, View>()
            output.writeStringUtf8(serialize(GetRenderInfo) + '\n')
            val initResponse = input.readUTF8Line()!!
            val initMap = deserialize(initResponse) as RenderInfo
            val colorManager = ColorManager()

            for (i in initMap.entities)
                when (i) {
                    is BOOM -> {
                        if (!i.started) {
                            val sprite = Sprite(boomAnimation)
                            sprite.anchor(0.5, 0.5)
                            sprite.x = i.x
                            sprite.y = i.y
                            sprite.playAnimation(spriteDisplayTime = Configuration.boomDuration.seconds)
                            i.started = true
                            addChild(sprite)
                            graphicsMap[i.id] = sprite
                        }
                    }
                    is Player -> {
                        val player = circle(playerSize, colorManager.getColor(i.team)).anchor(0.5, 0.5).xy(i.x, i.y)
                        graphicsMap[i.id] = player
                    }
                    //TODO: delete after adding tilemaps
                    is Object -> {
                        val wall = solidRect(objectSize, objectSize, Colors.BLACK).anchor(0.5, 0.5).xy(i.x, i.y)
                        graphicsMap[i.id] = wall
                    }
                    is Bullet -> {
                        val bullet = circle(bulletSize, Colors.ORANGERED).anchor(0.5, 0.5).xy(i.x, i.y)
                        graphicsMap[i.id] = bullet
                    }
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
                        graphicsMap[i.id]!!.xy(i.x, i.y)
                    } else {
                        when (i) {
                            is BOOM -> {
                                if (!i.started) {
                                    val sprite = Sprite(boomAnimation)
                                    sprite.anchor(0.5, 0.5)
                                    sprite.x = i.x
                                    sprite.y = i.y
                                    sprite.playAnimation(spriteDisplayTime = Configuration.boomDuration.seconds)
                                    i.started = true
                                    addChild(sprite)
                                    graphicsMap[i.id] = sprite
                                }
                            }
                            is Player -> {
                                val player = circle(playerSize, colorManager.getColor(i.team)).anchor(0.5, 0.5).xy(i.x, i.y)
                                graphicsMap[i.id] = player
                            }
                            //TODO: delete after adding tilemaps
                            is Object -> {
                                val wall = solidRect(objectSize, objectSize, Colors.BLACK).anchor(0.5, 0.5).xy(i.x, i.y)
                                graphicsMap[i.id] = wall
                            }
                            is Bullet -> {
                                val bullet = circle(bulletSize, Colors.ORANGERED).anchor(0.5, 0.5).xy(i.x, i.y)
                                graphicsMap[i.id] = bullet
                            }
                        }
                    }
                }

                val mX = mouseX
                val mY = mouseY

                output.writeStringUtf8(serialize(SetAngle(ClientServerPoint(mX, mY))) + '\n')
            }
        }
    }
}