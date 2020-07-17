package ru.leadpogrommer.mpg

import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.*

class Server(private val addr: String = "0.0.0.0", private val port: Int = 1337) {
    private val engine = Engine()

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
            runBlocking {
                engine.tick(16.0)
                delay(16L)
            }
        }
    }
}