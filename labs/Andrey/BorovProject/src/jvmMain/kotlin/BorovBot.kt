import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.math.atan2

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
                    server.channel.send(BorovMessageDirection(clid, a, CompletableDeferred<Boolean>()))
                }
            }
        }
    }
}