import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

class Client(val server: BorovServer) {
    lateinit var clid: String
    lateinit var player: BorovPlayer
    init {
        //
        GlobalScope.launch {
            val regcb = CompletableDeferred<String>()
            server.channel.send(BorovMessageRegister(regcb))
            clid = regcb.await()

            var playercb = CompletableDeferred<BorovPlayer?>()
            server.channel.send(BorovMessageInfo(clid, playercb))
            player = playercb.await() ?: throw Exception("im dead")

            while(true) {
                delay(1000)

                var playercb = CompletableDeferred<BorovPlayer?>()
                server.channel.send(BorovMessageInfo(clid, playercb))
                player = playercb.await() ?: throw Exception("im dead")

                // TODO: Change direction to follow player.target
            }
        }
    }
}