import com.soywiz.korge.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.withLock
import java.awt.Color

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
suspend fun main() = Korge(virtualHeight = 480, virtualWidth = 640) {
    solidRect(640, 480, Colors.WHITE)
    var user: UserClient = UserClient()

    launch {
        val server = startServer(this)
        withContext(Dispatchers.Default) {
            coroutineScope {
                user = UserClient(server)
                user.start(this)
                repeat(10) {
                    launch {
                        delay((1000 * Math.random()).toLong())
                        val c = BotClient(server)
                        c.start()
                    }
                }
            }
        }
    }

    val idToView = hashMapOf<Long, Circle>()

    fun drawPlayer(player: Player) {
        if (!idToView.containsKey(player.id)) {
            idToView[player.id] = Circle(Engine.playerRadius, Colors.DARKBLUE)
        }
        when(player.id){
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
    }

    addUpdater {
        if (Engine.changed) {
            runBlocking {
                Engine.mutex.withLock {
                    for (p in Engine.getPlayers()) {
                        drawPlayer(p.component2())
                    }
                }
            }
            Engine.changed = false
        }
    }
}