package ru.leadpogrommer.mpg

import com.soywiz.klock.DateTime
import com.soywiz.korma.geom.IPointInt
import com.soywiz.korma.geom.Point
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random
import org.mapeditor.core.*
import org.mapeditor.core.Map
import org.mapeditor.io.MapReader
import org.mapeditor.io.TMXMapReader
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round


@ObsoleteCoroutinesApi
fun CoroutineScope.engineActor() = actor<Message> {
    val clients = mutableMapOf<Communicator, Long>()
    val state = State()

    val map = TMXMapReader().readMap("..\\shared\\src\\jvmMain\\resources\\t.tmx")
    var obsLayerI = 0
    for(i in map.layers.indices){
        if(map.layers[i].name == "obstacles"){
            obsLayerI = i
        }
    }

    val obsts =  (map.layers[obsLayerI] as TileLayer)
    val colData = Array(map.height) {y -> BooleanArray(map.width) {x -> obsts.getTileAt(x, y) != null} }
    println()
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
        val delta = 0.016
        for(shit in state.getIterator()){
            val player = shit.value
            player.pos.x += player.vel.x * delta
            player.pos.y += player.vel.y * delta
            val ppx = player.pos.x / 32.0
            val ppy = player.pos.y / 32.0
            val ppos = Point(ppx, ppy)
            val ppr = player.radius / 32.0
            val currentCell = IPointInt(round(ppos.x).toInt(), round(ppos.y).toInt())
            for(y in max(0, currentCell.y - 1) .. min(map.height - 1, currentCell.y + 1)){
                for(x in max(0, currentCell.x - 1) .. min(map.width - 1, currentCell.x + 1)){
                    if(!colData[y][x])continue
                    val cx = x+0.5f
                    val cy = y+0.5f
                    val dst = ppos.distanceTo(Point(cx, cy))
                    if(dst < ppr + 0.5){
                        val b1 = Point(ppx - cx, ppy - cy).normalized.mul((ppr - dst + 0.5) / 2.0)
                        player.pos.add(b1.mul(64.0))
                    }
                }
            }
        }


        sendState()
    }

    suspend fun processRequest(id: Long, r: Request){
        when(r){

            is SetVelocityRequest -> {
                state.getPlayer(id).vel = r.vel
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