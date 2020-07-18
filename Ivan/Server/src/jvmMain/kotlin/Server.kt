import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import com.soywiz.korio.async.launch
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.ServerSocket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.util.KtorExperimentalAPI
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import java.net.InetSocketAddress

sealed class ServerMsg
class Register(val response: CompletableDeferred<Player>) : ServerMsg()
class ChangeDirection(var pId: Long, val dir: Vector) : ServerMsg()
class GetPlayers(val response: CompletableDeferred<HashMap<Long, Player>>) : ServerMsg()
object SetTargets : ServerMsg()
object Update : ServerMsg()

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
fun CoroutineScope.serverActor() = actor<ServerMsg> {
    val s = ServerActions()

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

class ServerActions{
    private val eng = Engine()

    private fun setTarget(player: Player) {
        for (p in eng.getPlayers()) {
            if (!(player === p.component2()) && !p.component2().busy && !(eng.getPlayers()[p.component2().targetId] === player)) {
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

    fun setTargets() {
        for (p in eng.getPlayers().filter { u -> (u.component2().targetId == 0L) }) {
            setTarget(p.component2())
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

class Server {
    val addr = "127.0.0.1"
    val port = 1221
    lateinit var serverSocket: ServerSocket
    lateinit var serverActor: SendChannel<ServerMsg>

    private var gson: Gson

    init {
        val shit = RuntimeTypeAdapterFactory.of(ServerMsg::class.java, "__type__")
        for(c in ServerMsg::class.sealedSubclasses){
            println("Registered class ${c.simpleName}")
            shit.registerSubtype(c.java, c.simpleName)
        }
        gson = GsonBuilder().registerTypeAdapterFactory(shit).create()
    }


    @KtorExperimentalAPI
    fun start() {
        serverSocket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().bind(InetSocketAddress(addr, port))
    }

    @ExperimentalCoroutinesApi
    @ObsoleteCoroutinesApi
    fun run(context: CoroutineScope) {
        runActor(context)
        runUpdater()
        runTargeter()
        runReceiver()
    }

    private fun runTargeter() {
        GlobalScope.launch {
            while(true){
                delay(5000)
                serverActor.send(SetTargets)
            }
        }
    }

    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    private fun runActor(context: CoroutineScope) {
        serverActor = context.serverActor()
    }

    private fun runUpdater() {
        GlobalScope.launch {
            while (true) {
                delay(16)
                serverActor.send(Update)
            }
        }
    }

    private fun runReceiver() {
        runBlocking {
            while (true) {
                val socket = serverSocket.accept()

                launch {
                    val futurePlayer = CompletableDeferred<Player>()
                    serverActor.send(Register(futurePlayer))
                    val p = futurePlayer.await()

                    val input = socket.openReadChannel()
                    while(true){
                        communicate(input, p)
                    }
                }
            }
        }
    }

    private suspend fun communicate(input: ByteReadChannel, p: Player) {
        var s = input.readUTF8Line()
        while (s == null) {
            s = input.readUTF8Line()
        }
        try {
            val msg = gson.fromJson(s, ServerMsg::class.java)
            if(msg is ChangeDirection){
                msg.pId = p.id
            }
            serverActor.send(msg)
        } catch (e: Exception){
            throw IllegalArgumentException("Unknown msg from client(socket)")
        }
    }

}