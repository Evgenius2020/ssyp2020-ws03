package ru.leadpogrommer.mpg

import com.soywiz.kmem.toInt
import com.soywiz.korev.Key
import com.soywiz.korge.Korge
import com.soywiz.korge.input.keys
import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onKeyDown
import com.soywiz.korge.input.onKeyUp
import com.soywiz.korge.tiled.readTiledMap
import com.soywiz.korge.tiled.tiledMapView
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.plus
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.random.Random

@KtorExperimentalAPI
suspend fun main() = Korge(title = "WTF", virtualWidth = 600, virtualHeight = 600) {
    val circles = mutableMapOf<Long, Circle>()
    val mapView = tiledMapView(resourcesVfs["t.tmx"].readTiledMap())
    val c = Camera()
    fun processRequest(r: Request){
        when(r){
            is StateRequest->{
                for (entry in r.state){
                    val id = entry.key
                    val player = entry.value
                    if (!circles.containsKey(id))circles[id] = circle(player.radius.toDouble()).anchor(0.5, 0.5)
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


//    val square = solidRect(100, 100, Colors.BLUE).apply {
//        anchor(0.5, 0.5)
//        addUpdater {
//            rotation += Angle(0.01)
//        }
////        xy(100, 100)
//    }

    val client = Communicator(aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect("127.0.0.1", 1337))
    client.run()
    launch {
        while (true) {
            val req = client.getRequests().receive()
            processRequest(req)
        }
    }

    fun sendSpeed(){
        val spd = 500.0
        val k = views.input.keys
        val nv = Point(k[Key.D].toInt()*spd - k[Key.A].toInt()*spd, k[Key.S].toInt()*spd - k[Key.W].toInt()*spd)
        launch {
            client.sendRequest(SetVelocityRequest(nv))
        }

    }
    onKeyDown { sendSpeed() }
    onKeyUp { sendSpeed() }




}





