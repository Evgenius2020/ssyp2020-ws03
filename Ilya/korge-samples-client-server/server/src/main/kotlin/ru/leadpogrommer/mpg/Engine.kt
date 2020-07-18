package ru.leadpogrommer.mpg

import com.soywiz.klock.DateTime
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

//import com.soywiz.klock.

class Engine {
    private val clients = mutableMapOf<Long, Client>()
    private val state = State()
    private var prevTickTime = DateTime.now()

    suspend fun addClient(c: Client){
        state.m.withLock {
            val ce = Player()
            state.registerPlayer(ce)
            c.sendRequest(LoginRequest(ce.id))
            clients[ce.id] = c
            println("Connected id ${ce.id}")
        }
    }

    suspend fun tick(){
        state.m.withLock {
            for (entry in clients){
                val ch = entry.value.getRequests()
                while(true){
                    val r = ch.poll() ?: break
                    processRequest(entry.key, r)
                }
            }

            if ((DateTime.now() - prevTickTime).milliseconds > 1000){
                for(en in state.getIterator()){
                    en.value.pos.x  = Random.nextDouble(500.0)
                    en.value.pos.y  = Random.nextDouble(500.0)
                }
                prevTickTime = DateTime.now()
            }


            sendState()
        }
    }


    private suspend fun processRequest(id: Long, r: Request){
        when(r){
            is ColorRequest -> {
                state.getPlayer(id).color = r.color
            }
            is DisconnectRequest ->{
                state.deletePlayer(id)
                clients.remove(id)
                for (client in clients.values){
                    client.sendRequest(DeletePlayerRequest(id))
                }
            }
        }
    }

    private suspend fun sendState(){
        for (client in clients.values){
            client.sendRequest(StateRequest(state.getSt()))
        }
    }
}