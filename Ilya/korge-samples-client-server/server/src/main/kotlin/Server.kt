import com.soywiz.korio.async.launch
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class Server(val addr: String = "0.0.0.0", val port: Int = 1337) {
    val engine = Engine()

    @KtorExperimentalAPI
    fun run(){
        val server = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().bind(addr, port)
            while (true){
                runBlocking {
                    val socket = server.accept()
                    val cl = Client(socket)
                    engine.addClient(cl)
                    cl.run()
                }
            }

    }
}