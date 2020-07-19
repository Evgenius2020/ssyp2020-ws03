package ru.leadpogrommer.mpg

import com.soywiz.klock.DateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random


@ObsoleteCoroutinesApi
fun CoroutineScope.engineActor() = actor<Message> {
    val clients = mutableMapOf<Communicator, Long>()
    val state = State()
    var prevTickTime = DateTime.now()

    suspend fun addClient(c: Communicator){
        val ce = Player()
        state.registerPlayer(ce)
        c.sendRequest(LoginRequest(ce.id))
        clients[c] = ce.id
        println("Connected id ${ce.id}")
    }

    suspend fun sendState(){
        for (client in clients.keys){
//            println("actually sent state")
            client.sendRequest(StateRequest(state.getSt()))
        }
    }

    suspend fun tick(){
//        println("tick")
        if ((DateTime.now() - prevTickTime).milliseconds > 1000){
            for(en in state.getIterator()){
                en.value.pos.x  = Random.nextDouble(500.0)
                en.value.pos.y  = Random.nextDouble(500.0)
            }
            prevTickTime = DateTime.now()
        }


        sendState()
    }

    suspend fun processRequest(id: Long, r: Request){
        when(r){
            is ColorRequest -> {
                state.getPlayer(id).color = r.color
            }
        }
    }

    for(msg in channel){
//        println(msg::class.simpleName)
        when(msg){
            is TickMsg -> tick()
            is RequestMsg -> processRequest(clients[msg.c]!!, msg.r)
            is ConnectMsg -> addClient(msg.c)
            is DisconnectMsg -> {
                val did = clients[msg.c]!!
                clients.remove(msg.c)
                state.deletePlayer(did)
                for(c in clients.keys){
                    c.sendRequest(DeletePlayerRequest(did))
                }
            }
        }
//        println("end msg")
    }
}