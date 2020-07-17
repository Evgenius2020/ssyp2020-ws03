import com.soywiz.korge.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.sync.withLock
import java.awt.Color

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
suspend fun main() = Korge(virtualHeight = 480, virtualWidth = 640) {
    solidRect(640, 480, Colors.WHITE)
    val serverManager = ServerManager()
    val futureChannel = CompletableDeferred<SendChannel<ServerMsg>>()

    launch {
        serverManager.startServer(this)
        futureChannel.complete(serverManager.channel)
        withContext(Dispatchers.Default) {
            coroutineScope {
                repeat(10) {
                    launch {
                        delay((1000 * Math.random()).toLong())
                        val c = BotClient(serverManager.channel)
                        c.start()
                    }
                }
            }
        }
    }

    val user = UserClient(futureChannel.await())
    launch {
        user.start(this)
    }

    val idToView = hashMapOf<Long, Circle>()

    fun drawPlayer(player: Player) {
        if (!idToView.containsKey(player.id)) {
            idToView[player.id] = Circle(10.0, Colors.DARKBLUE)
        }
        when (player.id) {
            user.targetId -> {
                idToView[player.id]!!.color = Colors.GREEN
            }
            user.killerId -> {
                idToView[player.id]!!.color = Colors.RED
            }
            user.pId -> {
                idToView[player.id]!!.color = Colors.BROWN
            }
            else -> {
                idToView[player.id]!!.color = Colors.DARKBLUE
            }
        }
        val c = idToView[player.id]!!
        c.xy(player.pos.x, player.pos.y)
        addChild(c)
    }

    addUpdater {
        launch {
            user.changeDirection(Dot(mouseX, mouseY))
        }
    }

    addUpdater {
        launch {
            val request = CompletableDeferred<HashMap<Long, Player>>()
            serverManager.channel.send(GetPlayers(request))
            for (p in request.await()) {
                drawPlayer(p.component2())
            }
        }
    }
}