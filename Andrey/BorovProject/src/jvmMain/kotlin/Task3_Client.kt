import com.soywiz.korge.Korge
import com.soywiz.korge.view.Circle
import com.soywiz.korge.view.xy
import com.soywiz.korim.color.Colors
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.Math.random
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.tan

class BorovClient(val server: BorovServer) {
    suspend fun start() = Korge(width = 512, height = 512, bgcolor = Colors["#2b2b2b"]) {
        while(true) {
            val clidcb = CompletableDeferred<String>()
            server.channel.send(BorovMessageRegister(clidcb))
            val clid = clidcb.await()

            while(true) {
                // TODO: Implement client loop
            }
        }
    }
}

class BorovBot(val server: BorovServer) {
    lateinit var clid: String
    lateinit var player: BorovPlayer
    init {
        GlobalScope.launch {
            val regcb = CompletableDeferred<String>()
            server.channel.send(BorovMessageRegister(regcb))
            clid = regcb.await()

            while(true) {
                delay(50)

                var playercb = CompletableDeferred<BorovPlayer?>()
                server.channel.send(BorovMessageInfo(clid, playercb))
                player = playercb.await() ?: throw Exception("im dead")

                var mapcb = CompletableDeferred<HashMap<String, BorovPlayer>>()
                server.channel.send(BorovMessageMap(mapcb))
                val map = mapcb.await()

                if(player!!.target != null && map.containsKey(player.target)) {
                    val target = map[player.target]
                    val dx = target!!.x - player.x
                    val dy = target!!.y - player.y
                    val a = atan2(dy, dx)
                    println(a)
                    server.channel.send(BorovMessageDirection(clid, a, CompletableDeferred<Boolean>()))
                }
            }
        }
    }
}