import com.soywiz.kmem.toInt
import com.soywiz.korau.sound.NativeSoundChannel
import com.soywiz.korau.sound.readMusic
import com.soywiz.korev.Key
import com.soywiz.korge.input.keys
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.service.process.NativeProcess
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.scale
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.async.runBlockingNoSuspensions
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korlibs.samples.clientserver.Action
import com.soywiz.korma.geom.Point
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.SelectClause1
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.random.Random

class GameScene(val ip: String, val port: Int) : Scene() {
    lateinit var music: NativeSoundChannel
    lateinit var ball: Circle
    lateinit var overlay: GameOverOverlay
    var numObstacles = 0
    lateinit var bmp: Bitmap

    var cCount = 0


    lateinit var client: Client

    var id: Long = -1

    val vs = mutableMapOf<Long, NpcView>()


    val vsmtx = Mutex()

    @KtorExperimentalAPI
    suspend override fun Container.sceneInit() {
        client = Client(aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(ip, port))
        client.run()
        val shit = client.getRequests().receive()
        id = (shit.args[0] as Double).toLong()
        println("Got id $id")
        GlobalScope.launch() {
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


    suspend fun sendSpeed(){
//        println("sent speed")
        val k = views.input.keys
        val spd = Point(k[Key.D].toInt() - k[Key.A].toInt(), k[Key.S].toInt() - k[Key.W].toInt())

        client.sendRequest(Request(Action.MOVE, arrayOf(spd)))
    }

    fun entityFromShit(shit: Map<String, Any>): Entity{
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


    suspend fun processRequest(r: Request){
        when(r.a){
            Action.SET_STATE -> {
//                println("Got state")
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
//                            println("${x.toInt()}\t${y.toInt()}")
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
