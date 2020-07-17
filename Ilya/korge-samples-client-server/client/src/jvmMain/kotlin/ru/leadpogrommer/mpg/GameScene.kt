package ru.leadpogrommer.mpg

import com.soywiz.kmem.toInt
import com.soywiz.korev.Key
import com.soywiz.korge.input.keys
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.addUpdater
import com.soywiz.korge.view.position
import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.Point
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.collections.set

class GameScene(private val ip: String, private val port: Int) : Scene() {
    private lateinit var client: Client
    private var id: Long = -1
    private val vs = mutableMapOf<Long, NpcView>()
    private val vsmtx = Mutex()

    @KtorExperimentalAPI
    override suspend fun Container.sceneInit() {
        client = Client(aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(ip, port))
        client.run()
        val shit = client.getRequests().receive()
        id = (shit.args[0] as Double).toLong()
        println("Got id $id")
        GlobalScope.launch {
            while(true){
                val req = client.getRequests().receive()
                vsmtx.withLock {
                    processRequest(req)
                }
            }
        }

        keys {
            down { sendSpeed() }
            up { sendSpeed() }
        }


    }


    private suspend fun sendSpeed(){
        val k = views.input.keys
        val spd = Point(k[Key.D].toInt() - k[Key.A].toInt(), k[Key.S].toInt() - k[Key.W].toInt())

        client.sendRequest(Request(Action.MOVE, arrayOf(spd)))
    }

    private fun entityFromShit(shit: Map<String, Any>): Entity {
        val ent = Entity()
        ent.id = (shit["id"]!! as Double).toLong()
        fun pissToPoint(_p: Any): Point{
            val p = _p as Map<String, Double>
            val o = Point()
            o.x = p["x"]!!
            o.y = p["y"]!!
            return o
        }
        ent.pos = pissToPoint(shit["pos"]!!)
        ent.vel = pissToPoint(shit["vel"]!!)
        return ent

    }


    private fun processRequest(r: Request){
        when(r.a){
            Action.SET_STATE -> {
                val starr = r.args[0] as  Map<String, Any>

                starr.forEach { shit ->
                    val entity = entityFromShit(shit.value as Map<String, Any>)
                    if ( !vs.containsKey(entity.id)){
                        val crcl = NpcView(10.0, Colors.BLUE)
                        if (entity.id == id){
                            crcl.color = Colors.RED
                        }
                        crcl.addUpdater {
                            x += vel.x * it.seconds
                            y += vel.y * it.seconds
                        }
                        vs[entity.id] = crcl
                        sceneView.addChild(crcl)
                    }
                    val n = vs[entity.id]!!
                    n.position(entity.pos)
                    n.vel = entity.vel

                }
            }
        }
    }
}
