import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.*
import io.ktor.util.KtorExperimentalAPI
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.readUTF8Line
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import java.net.InetSocketAddress

sealed class ServerMsg(var __type__: String = "")
class Register(val response: CompletableDeferred<Player>) : ServerMsg(Register::class.java.simpleName)
class ChangeDirection(var pId: Long? = null, val dir: Vector) : ServerMsg(ChangeDirection::class.java.simpleName)
class GetPlayers(var response: CompletableDeferred<HashMap<Long, Player>>? = null) : ServerMsg(GetPlayers::class.java.simpleName)
class Disconnect(var pId: Long) : ServerMsg(Disconnect::class.java.simpleName)
object SetTargets : ServerMsg(SetTargets::class.java.simpleName)
object Update : ServerMsg(Update::class.java.simpleName)
class Players(val players: HashMap<Long, Player>) : ServerMsg(Players::class.java.simpleName)

class Client(var addr: String = "127.0.0.1", var port: Int = 1221){

    var gson: Gson

    lateinit var socket: Socket
    lateinit var input: ByteReadChannel
    lateinit var output: ByteWriteChannel

    init {
        val shit = RuntimeTypeAdapterFactory.of(ServerMsg::class.java, "__type__")
        for(c in ServerMsg::class.sealedSubclasses){
            println("Registered class ${c.simpleName}")
            shit.registerSubtype(c.java, c.simpleName)
        }
        gson = GsonBuilder().registerTypeAdapterFactory(shit).create()
    }

    @KtorExperimentalAPI
    suspend fun start(){
        socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress(addr, port))
        input = socket.openReadChannel()
        output = socket.openWriteChannel(autoFlush = true)
    }

    suspend fun getPlayers(): HashMap<Long, Player>{
        output.writeStringUtf8(gson.toJson(GetPlayers()))
        println("printed")

        var s = input.readUTF8Line()
        while(s == null) {
            s = input.readUTF8Line()
        }
        return gson.fromJson(s, Players::class.java).players
    }

    suspend fun changeDirection(dir: Vector){
        output.writeStringUtf8(gson.toJson(ChangeDirection(dir = dir)))
    }
}