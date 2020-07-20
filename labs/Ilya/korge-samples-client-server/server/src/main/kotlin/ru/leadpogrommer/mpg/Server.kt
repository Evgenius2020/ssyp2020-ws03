package ru.leadpogrommer.mpg

import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.ticker

class Server(private val addr: String = "0.0.0.0", private val port: Int = 1337) {
    @ObsoleteCoroutinesApi
    private val engine = GlobalScope.engineActor()

    @KtorExperimentalAPI
    fun run()= runBlocking{
        GlobalScope.launch {
            while (true){
                engine.send(TickMsg())
                delay(16L)
            }

        }
        val server = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().bind(addr, port)
        while (true){
            val socket = server.accept()
            val cl = Communicator(socket)
            engine.send(ConnectMsg(cl))
            launch(Dispatchers.IO){
                try {
                    while (true){
                        engine.send(RequestMsg(cl, cl.getRequests().receive()))
                    }
                }catch (e: ClosedReceiveChannelException){
                    engine.send(DisconnectMsg(cl))
                }

            }
            cl.run()
        }
    }
}