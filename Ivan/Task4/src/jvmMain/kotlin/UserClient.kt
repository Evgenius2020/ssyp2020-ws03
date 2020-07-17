import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UserClient(var server: SendChannel<ServerMsg>? = null) {
    var targetId: Long = 0
    var killerId: Long = 0
    var pId = 0L
    var pos:Dot = Dot(0.0, 0.0)

    suspend fun changeDirection(targetPos: Dot){
        if(server == null) return

        server!!.send(ChangeDirection(pId, pos.directionTo(targetPos)))
    }

    suspend fun start(context: CoroutineScope){
        if(server == null) return
        val request = CompletableDeferred<Player>()
        server!!.send(Register(request))
        val p = request.await()
        pId = p.id
        targetId = p.targetId
        lookForTarget(context)
    }

    private fun lookForTarget(context: CoroutineScope){
        context.launch {
            while(true){
                delay(16)
                val playersRequest = CompletableDeferred<HashMap<Long, Player>>()
                server!!.send(GetPlayers(playersRequest))
                val players = playersRequest.await()
                if(!players.containsKey(pId)){
                    continue
                }
                targetId = players[pId]!!.targetId
                pos = players[pId]!!.pos
                println("Here")
                for(p in players){
                    if(p.component2().targetId == pId){
                        killerId = p.component1()
                    }
                }
            }
        }
    }
}