import com.soywiz.korlibs.samples.clientserver.Action
import com.soywiz.korma.geom.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.withLock

class Engine {
    val clients = mutableMapOf<Long, Client>()
    val state = State()

    suspend fun addClient(c: Client){
        state.m.withLock {
            val ce = Entity()
            state.registerEntity(ce)
            c.sendRequest(Request(Action.LOGIN_ACC, arrayOf(ce.id)))
            clients[ce.id] = c
            println("Connected id ${ce.id}")
        }

    }


    suspend fun tick(delta: Double){

        state.m.withLock {
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


    }


    suspend fun processRequest(id: Long, r: Request){
        when(r.a){
            Action.MOVE -> {
                val mp = r.args[0] as Map<String, Double>
                var nv = Point(mp["x"]!!, mp["y"]!!)
//                println("got spedd $nv")
                nv = nv.normalized.mul(0.6)
                if(nv.x.isNaN() || nv.y.isNaN())nv = Point(0 ,0)
                state.getEntity(id)!!.vel = nv
            }
        }
    }

    suspend fun sendState(){
        val st = state.getSt()
        for (client in clients.values){
//            println("sent state")
            client.sendRequest(Request(Action.SET_STATE, arrayOf(st)))
        }


    }

}