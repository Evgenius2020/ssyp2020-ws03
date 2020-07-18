import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.ServerSocket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.util.KtorExperimentalAPI
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.readUTF8Line
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import java.io.IOException
import java.net.InetSocketAddress

sealed class ServerMsg(var __type__: String = "")
class Register(val response: CompletableDeferred<Player>) : ServerMsg(Register::class.java.simpleName)
class ChangeDirection(var pId: Long? = null, val dir: Vector) : ServerMsg(ChangeDirection::class.java.simpleName)
class GetPlayers(var response: CompletableDeferred<HashMap<Long, Player>>? = null) : ServerMsg(GetPlayers::class.java.simpleName)
class Disconnect(var pId: Long) : ServerMsg(Disconnect::class.java.simpleName)
object SetTargets : ServerMsg(SetTargets::class.java.simpleName)
object Update : ServerMsg(Update::class.java.simpleName)
class Players(val players: HashMap<Long, Player>) : ServerMsg(Players::class.java.simpleName)

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
fun CoroutineScope.serverActor() = actor<ServerMsg> {
    val s = ServerActions()

    for (msg in channel) {
        if (!isClosedForReceive) {
            when (msg) {
                is Register -> s.register(msg.response)
                is ChangeDirection -> s.changeDirection(msg.pId!!, msg.dir)
                is GetPlayers -> s.getPlayers(msg.response!!)
                is Update -> s.update()
                is SetTargets -> s.setTargets()
                is Disconnect -> s.disconnect(msg.pId)
            }
        }
    }
}

class ServerActions {
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

    fun disconnect(pId: Long){
        eng.disconnect(pId)
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
        for (c in ServerMsg::class.sealedSubclasses) {
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
        runUpdater(context)
        runTargeter(context)
        runReceiver(context)
    }

    private fun runTargeter(context: CoroutineScope) {
        context.launch {
            while (true) {
                delay(5000)
                serverActor.send(SetTargets)
            }
        }
    }

    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    private fun runActor(context: CoroutineScope) {
        context.launch {
            serverActor = serverActor()
        }
    }

    private fun runUpdater(context: CoroutineScope) {
        context.launch {
            while (true) {
                delay(16)
                serverActor.send(Update)
            }
        }
    }

    private fun runReceiver(context: CoroutineScope) {
        context.launch {
            while (true) {
                val socket = serverSocket.accept()
                print("Connected")

                launch {
                    println("Register...")
                    val futurePlayer = CompletableDeferred<Player>()
                    serverActor.send(Register(futurePlayer))
                    val p = futurePlayer.await()
                    println("Registered")

                    val input = socket.openReadChannel()
                    val output = socket.openWriteChannel()

                    while (true) {
                        try {
                            println("communicate")
                            communicate(input, output, p)
                        } catch (e: IOException) {
                            serverActor.send(Disconnect(p.id))
                        }
                    }
                }
            }
        }
    }

    private suspend fun communicate(input: ByteReadChannel, output: ByteWriteChannel, p: Player) {
        var s = input.readUTF8Line()
        println(s)
        println("receiving")
        while (s == null) {
            s = input.readUTF8Line()
        }
        println(s)
        try {
            val msg = gson.fromJson(s, ServerMsg::class.java)
            if (msg is GetPlayers) {
                val response = CompletableDeferred<HashMap<Long, Player>>()
                msg.response = response
                serverActor.send(msg)
                val players = response.await()
                output.writeStringUtf8(gson.toJson(players))
            } else {
                if (msg is ChangeDirection) {
                    msg.pId = p.id
                }
            }
            serverActor.send(msg)
        } catch (e: Exception) {
            throw IllegalArgumentException("Unknown msg from client(socket)")
        }
    }

}