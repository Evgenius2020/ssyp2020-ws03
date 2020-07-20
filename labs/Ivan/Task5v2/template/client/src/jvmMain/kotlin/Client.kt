import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
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
import java.net.InetSocketAddress

class Client(var addr: String = "127.0.0.1", var port: Int = 1221){

    var gson: Gson

    lateinit var socket: Socket
    lateinit var input: ByteReadChannel
    lateinit var output: ByteWriteChannel

    init {
        val shit = RuntimeTypeAdapterFactory.of(ServerMsg::class.java, "__type__")
        for(c in ServerMsg::class.sealedSubclasses){
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
        output.writeStringUtf8(gson.toJson(GetPlayers()) + "\n")


        var s = input.readUTF8Line()
        while(s == null) {
            s = input.readUTF8Line()
        }
        val players = gson.fromJson(s, Players::class.java)
        return players.players
    }

    suspend fun changeDirection(dot: Dot){
        output.writeStringUtf8(gson.toJson(ChangeDirection(dot = dot)) + "\n")
    }
}