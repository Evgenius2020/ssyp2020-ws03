package ru.leadpogrommer.mpg

import com.soywiz.korge.Korge
import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.Circle
import com.soywiz.korge.view.circle
import com.soywiz.korge.view.xy
import com.soywiz.korim.color.RGBA
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

@KtorExperimentalAPI
suspend fun main() = Korge(title = "WTF", virtualWidth = 600, virtualHeight = 600, height = 300, width = 300) {
    val circles = mutableMapOf<Long, Circle>()

    fun processRequest(r: Request){
        when(r){
            is StateRequest->{
                for (entry in r.state){
                    val id = entry.key
                    val player = entry.value
                    if (!circles.containsKey(id))circles[id] = circle(10.0)
                    circles[id]!!.color = player.color
                    circles[id]!!.xy(player.pos.x, player.pos.y)
                }
            }
            is DeletePlayerRequest ->{
                circles[r.id]?.removeFromParent()
                circles.remove(r.id)
            }
        }
    }


    val client = Communicator(aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect("127.0.0.1", 1337))
    client.run()
    launch {
        while (true) {
            val req = client.getRequests().receive()
            processRequest(req)
        }
    }

    onClick {
        val nc = RGBA(Random.nextInt(100, 255), Random.nextInt(100, 255), Random.nextInt(100, 255), 255)
        client.sendRequest(ColorRequest(nc))
    }



}





