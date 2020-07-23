package client

import com.soywiz.klock.seconds
import com.soywiz.korev.Key
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
import kotlin.math.max

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
//            val spriteMap = resourcesVfs["BOOM.png"].readBitmap()

//            val boomAnimation = SpriteAnimation(
//                    spriteMap = spriteMap,
//                    spriteWidth = 960 / 5,
//                    spriteHeight = 384 / 2,
//                    columns = 5,
//                    rows = 2
//            )

            val mapView = TiledMapView(resourcesVfs["map.tmx"].readTiledMap())
            addChild(mapView)

            val fps = text("", 20.0).xy(0, 0)
            fps.addUpdater {
                fps.text = (1000 / it.milliseconds).toInt().toString()
            }

            views.root.onClick {
                output.writeStringUtf8(serialize(Shoot) + '\n')
            }

            val graphicsMap = mutableMapOf<Int, List<View>>()

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
                        for (l in item.value)
                            removeChild(l)
                        iterator.remove()
                    }
                }

                for (i in map.entities) {
                    if (i.id in graphicsMap) {
                        when (i) {
                            is Player -> {
                                graphicsMap[i.id]!![0].xy(i.x, i.y).rotation(Angle(i.angle))
                                graphicsMap[i.id]!![1].xy(i.x - 16, i.y - 50)
                                graphicsMap[i.id]!![2].xy(i.x - 16, i.y - 50)
                                graphicsMap[i.id]!![2].width = max(0.3 * i.health, 0.0)
                                graphicsMap[i.id]!![3].xy(i.x - 16, i.x - 45)
                                graphicsMap[i.id]!![4].xy(i.x - 16, i.x - 45)
                                graphicsMap[i.id]!![4].width = 30 - (map.shootCooldown * 30)
                                graphicsMap[i.id]!![5].centerOn(graphicsMap[i.id]!![1])
                                graphicsMap[i.id]!![5].y -= 10
                            }
                            else -> graphicsMap[i.id]!![0].xy(i.x, i.y).rotation(Angle(i.angle))
                        }

                    } else {
                        when (i) {
                            is BOOM -> {
                             /*   if (!i.started) {
                                    val sprite = Sprite(boomAnimation)
                                    sprite.anchor(0.5, 0.5)
                                    sprite.x = i.x
                                    sprite.y = i.y
                                    sprite.playAnimation(spriteDisplayTime = Configuration.boomDuration.seconds)
                                    i.started = true
                                    addChild(sprite)
                                    graphicsMap[i.id] = listOf(sprite)
                                }*/
                            }
                            is Player -> {
                                val player = image(resourcesVfs["team${map.teamsMap[i.team]}.png"].readBitmap()).anchor(0.3, 0.5).xy(i.x, i.y).rotation(Angle(i.angle))
                                val healthbarD = solidRect(30, 5, Colors.DARKGRAY).xy(i.x - 16, i.y - 50)
                                val healthbarT = solidRect(30, 5, Colors.RED).xy(i.x - 16, i.y - 50)

                                val cooldownD = solidRect(30, 5, Colors.DARKGRAY).xy(i.x - 16, i.y - 45)
                                val cooldownT = solidRect(30, 5, Colors.LIGHTGRAY).xy(i.x - 16, i.y - 45)

                                val nick = text(i.nick, 10.0, color = Colors.BLACK).centerOn(healthbarD)
                                nick.y -= 10
                                player.height = 32.0
                                player.width = 40.0
                                graphicsMap[i.id] = listOf(player, healthbarD, healthbarT, cooldownD, cooldownT, nick)
                            }
                            is Bullet -> {
                                val bullet = circle(bulletSize, Colors.ORANGERED).anchor(0.5, 0.5).xy(i.x, i.y)
                                graphicsMap[i.id] = listOf(bullet)
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