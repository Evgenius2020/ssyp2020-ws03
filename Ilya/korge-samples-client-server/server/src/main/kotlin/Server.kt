import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class Server(val addr: String = "0.0.0.0", val port: Int = 1337) {
    val engine = Engine()

    @KtorExperimentalAPI
    fun run(){
        GlobalScope.launch(Dispatchers.IO){
            val server = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().bind(addr, port)
            while (true){

                    val socket = server.accept()
                    val cl = Client(socket)
                    engine.addClient(cl)
                    cl.run()
            }
        }
        var prev = 16L
        while (true){
//            prev = measureTimeMillis { runBlocking { engine.tick(prev.toDouble()) }}
            runBlocking {
                engine.tick(16.0)
                delay(16L)
            }
        }
    }
}