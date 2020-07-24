package client.scenes

import com.soywiz.klock.seconds
import com.soywiz.kmem.toInt
import com.soywiz.korev.Key
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.tiled.TiledMapView
import com.soywiz.korge.tiled.readTiledMap
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.Angle
import engine.Configuration
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.util.KtorExperimentalAPI
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.readUTF8Line
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.Dispatchers
import shared.*
import java.net.InetSocketAddress
import kotlin.math.max
import kotlin.math.min

class GameScene(val nick: String) : Scene() {

    private lateinit var boomAnimation: SpriteAnimation
    private lateinit var gameTimer: Text
    private lateinit var fpsText: Text
    private lateinit var socket: Socket
    private lateinit var input: ByteReadChannel
    private lateinit var output: ByteWriteChannel
    private lateinit var tiledMap: TiledMapView
    private lateinit var graphicsMap: MutableMap<Int, List<View>>
    private lateinit var statisticCenter: View
    private var statistics: Triple<View, MutableList<View>, MutableList<View>>? = null
    private lateinit var respawnTimer: Text

    @KtorExperimentalAPI
    override suspend fun Container.sceneInit() {
        socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("127.0.0.1", 1221))
        input = socket.openReadChannel()
        output = socket.openWriteChannel(autoFlush = true)

        output.writeStringUtf8(serialize(Register(nick)) + '\n')

        boomAnimation = SpriteAnimation(
                spriteMap = resourcesVfs["BOOM.png"].readBitmap(),
                spriteWidth = 960 / 5,
                spriteHeight = 384 / 2,
                columns = 5,
                rows = 2
        )

        tiledMap = TiledMapView(resourcesVfs["map.tmx"].readTiledMap())
        addChild(tiledMap)

        val gameTimerAligner = solidRect(1, 1, Colors.BLACK).xy(width, 0.0)
        gameTimer = text("", 20.0) {
            filtering = false
            addUpdater {
                alignRightToRightOf(gameTimerAligner)
            }
        }

        fpsText = text("", 20.0) {
            filtering = false
            position(0.0, 0.0)
            addUpdater {
                text = (1000 / it.milliseconds).toInt().toString()
            }
        }

        graphicsMap = mutableMapOf()

        // Shoot

        views.root.onClick {
            output.writeStringUtf8(serialize(Shoot) + '\n')
        }

        statisticCenter = solidRect(1, views.virtualHeight, RGBA(0, 0, 0, 0)) {
            addUpdater {
                position(views.virtualWidth / 2, views.virtualHeight / 2)
            }
        }

    }

    override suspend fun Container.sceneMain() {
        while (true) {
            //New Map From server
            output.writeStringUtf8(serialize(GetRenderInfo) + '\n')
            val response = input.readUTF8Line()!!
            val map = deserialize(response) as RenderInfo

            //Update game timer
            gameTimer.text = map.endGameTimer.toString()

            // All existing entities
            val exist = mutableListOf<Int>()

            for (i in map.entities)
                exist.add(i.id)

            // Delete all non-existing entities
            val iterator = graphicsMap.iterator()
            while (iterator.hasNext()) {
                val item = iterator.next()
                if (item.key !in exist) {
                    for (l in item.value)
                        removeChild(l)
                    iterator.remove()
                }
            }

            // Update/create entities
            for (i in map.entities) {
                if (i.id in graphicsMap) {
                    when (i) {
                        is Player -> {
                            graphicsMap[i.id]!![0].xy(i.x, i.y).rotation(Angle(i.angle))
                            graphicsMap[i.id]!![1].xy(i.x - 16, i.y - 50)
                            graphicsMap[i.id]!![2].xy(i.x - 16, i.y - 50)
                            graphicsMap[i.id]!![2].width = max(0.3 * i.health, 0.0)
                            if (i.id == map.pId) {
                                graphicsMap[i.id]!![4].xy(i.x - 16, i.y - 45)
                                graphicsMap[i.id]!![5].xy(i.x - 16, i.y - 45)
                                graphicsMap[i.id]!![5].width = min(30 - (map.shootCooldown * 30), 30.0)
                            }

                            graphicsMap[i.id]!![3].centerOn(graphicsMap[i.id]!![1])
                            graphicsMap[i.id]!![3].y -= 10
                        }
                        else -> graphicsMap[i.id]!![0].xy(i.x, i.y).rotation(Angle(i.angle))
                    }

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
                                graphicsMap[i.id] = listOf(sprite)
                            }
                        }
                        is Player -> {
                            val player = image(resourcesVfs["team${map.teamsMap[i.team]}.png"].readBitmap()).anchor(0.3, 0.5).xy(i.x, i.y).rotation(Angle(i.angle))
                            val healthbarD = solidRect(30, 5, RGBA(45, 52, 54, 255)).xy(i.x - 16, i.y - 50)
                            val healthbarT = solidRect(30, 5, RGBA(214, 48, 49, 255)).xy(i.x - 16, i.y - 50)

                            val nick = text(i.nick, 10.0, color = Colors.BLACK).centerOn(healthbarD)

                            nick.y -= 10
                            player.height = 32.0
                            player.width = 40.0

                            if (map.pId == i.id) {
                                val cooldownD = solidRect(30, 5, RGBA(45, 52, 54, 255)).xy(i.x - 16, i.y - 45)
                                val cooldownT = solidRect(30, 5, RGBA(178, 190, 195, 252)).xy(i.x - 16, i.y - 45)
                                graphicsMap[i.id] = listOf(player, healthbarD, healthbarT, nick, cooldownD, cooldownT)
                            } else
                                graphicsMap[i.id] = listOf(player, healthbarD, healthbarT, nick)

                        }
                        is Bullet -> {
                            val bullet = circle(Configuration.radiusOfBullet, Colors.ORANGERED).anchor(0.5, 0.5).xy(i.x, i.y)
                            graphicsMap[i.id] = listOf(bullet)
                        }
                    }
                }
            }

            // Rotation

            val mX = localMouseX(views)
            val mY = localMouseY(views)

            output.writeStringUtf8(serialize(SetAngle(ClientServerPoint(mX, mY))) + '\n')

            // Move

            val inputWASD = views.input.keys
            val x = (inputWASD[Key.D].toInt()) - (inputWASD[Key.A].toInt())
            val y = (-inputWASD[Key.W].toInt()) + (inputWASD[Key.S].toInt())
            output.writeStringUtf8(serialize(ChangeSpeed(x, y)) + '\n')

            output.writeStringUtf8(serialize(GetStatistic) + '\n')

        }
    }
}