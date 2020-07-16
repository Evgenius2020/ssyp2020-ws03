import com.soywiz.korlibs.samples.clientserver.Action
import com.soywiz.korma.geom.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Engine {
    val clients = mutableMapOf<Long, Client>()
    val state = State()

    suspend fun addClient(c: Client){
        val ce = Entity()
        state.registerEntity(ce)
        c.sendRequest(Request(Action.LOGIN_ACC, arrayOf(ce.id)))
        clients[ce.id] = c
    }


    suspend fun tick(delta: Double){
        for (entry in clients){
            val ch = entry.value.getRequests()
            while(true){
                val r = ch.poll() ?: break
                processRequest(entry.key, r)
            }
        }

        for(en in state.getIterator()){
            en.value.pos.x += en.value.vel.x * delta
            en.value.pos.y += en.value.vel.y * delta
        }

        sendState()

    }


    fun processRequest(id: Long, r: Request){
        when(r.a){
            Action.MOVE -> state.getEntity(id)!!.vel = (r.args[0] as Point)
        }
    }

    suspend fun sendState(){
        val st = state.getSt()
        for (client in clients.values){
            client.sendRequest(Request(Action.SET_STATE, arrayOf(st)))
        }

    }

}