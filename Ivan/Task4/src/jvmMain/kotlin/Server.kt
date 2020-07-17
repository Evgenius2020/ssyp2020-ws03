import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.sync.withLock

sealed class ServerMsg
class Register(val response: CompletableDeferred<Player>) : ServerMsg()
class ChangeDirection(val pId: Long, val dir: Vector) : ServerMsg()
class GetPlayers(val response: CompletableDeferred<HashMap<Long, Player>>) : ServerMsg()
object SetTargets : ServerMsg()
object Update : ServerMsg()

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
suspend fun startServer(context: CoroutineScope): SendChannel<ServerMsg> {
    val server = context.serverActor()
    context.launch {
        while (true) {
            delay(16)
            if (server.isClosedForSend) {
                break
            } else {
                server.send(Update)
            }
        }
    }
    context.launch {
        while(true){
            delay(5000)
            if(server.isClosedForSend) {
                break
            } else{
                server.send(SetTargets)
            }
        }
    }
    return server
}

@ObsoleteCoroutinesApi
fun CoroutineScope.serverActor() = actor<ServerMsg> {
    val s = Server

    for (msg in channel) {
        if (!isClosedForReceive) {
            when (msg) {
                is Register -> s.register(msg.response)
                is ChangeDirection -> s.changeDirection(msg.pId, msg.dir)
                is GetPlayers -> s.getPlayers(msg.response)
                is Update -> s.update()
                is SetTargets -> s.setTargets()
            }
        }
    }
}

object Server {
    init {
        Engine.clear()
    }

    val eng = Engine

    private fun setTarget(player: Player) {
        for (p in Engine.getPlayers()) {
            if (!(player === p.component2()) && !p.component2().busy && !(Engine.getPlayers()[p.component2().targetId] === player)) {
                player.targetId = p.component1()
                p.component2().busy = true
                break
            }
        }
    }

    fun register(response: CompletableDeferred<Player>) {
        val p = eng.addPlayer()
        setTarget(p)
        response.complete(p)
    }

    suspend fun setTargets() {
        eng.mutex.withLock {
            for (p in eng.getPlayers().filter { u -> (u.component2().targetId == 0L) }) {
                setTarget(p.component2())
            }
        }
    }

    fun update() {
        eng.nextState()
    }

    fun changeDirection(pId: Long, dir: Vector) {
        eng.changeDirection(pId, dir)
    }

    fun getPlayers(response: CompletableDeferred<HashMap<Long, Player>>) {
        response.complete(eng.getPlayers())
    }
}