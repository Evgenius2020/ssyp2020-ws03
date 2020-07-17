import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.concurrent.thread
import kotlin.math.*

val mutex = Mutex()

class Server {
    val eng = Engine()
    fun addPl(): Int{
        eng.addPlayer()
        return eng.numOfIds-1
    }
    fun getTarget(id: Int): Int{
        var pl: Any
        pl = if (id != 0){
            eng.findPl(id-1)!!
        } else{
            eng.findPl(eng.numOfIds-1)!!
        }
        if((pl.isTarget == 0) && (pl.id != id) && (pl.haveTarget != id)){
            pl.isTarget = 1
            val pla = eng.findPl(id)!!
            pla.haveTarget = pl.id
            return pl.id
        }
        return -1
    }
    fun run() {
        GlobalScope.launch {
            while(true){
                delay(100)
                eng.movePlayers()
            }
        }
    }
    fun getInfX(): MutableList<Double>{
        val a = mutableListOf<Double>()
        for (pl in eng.players){
            a.add(pl.x)
        }
        return a
    }
    fun getInfY(): MutableList<Double>{
        val a = mutableListOf<Double>()
        for (i in eng.players){
            a.add(i.y)
        }
        return a
    }
    fun changeDir(id: Int, angleToTar: Double){
        eng.changeDir(id, angleToTar)
    }
    fun updPl(id: Int, mul: Double){
        eng.updatePlayerState(id, mul)
    }
}

class Client{
    private var id = -1
    private var target = -1
    private var listX = mutableListOf<Double>()
    private var listY = mutableListOf<Double>()
    private var angleToTarget = -1.0
    fun play(ser: Server) {
        id = ser.addPl()
        fun gettar(){
            while (target == -1) {
                runBlocking {
                    mutex.withLock {
                        target = ser.getTarget(id)
                    }
                    println("Client get tar: $id $target $angleToTarget")
                    delay(1000)
                }
            }
        }
        gettar()
        while (true) {
            if (target != -1){
                println("Client: $id $target $angleToTarget")
                runBlocking {
                    mutex.withLock {
                        listX = ser.getInfX()
                        listY = ser.getInfY()
                    }
                    if (sqrt(
                            (listX[id] - listX[target]).pow(2.0) +
                                    (listY[id] - listY[target]).pow(2.0)
                        ) < 20.0)
                    {
                        mutex.withLock {
                            ser.updPl(id, 1.0)
                            ser.updPl(target, -1.0)
                        }
                        target = -1
                        gettar()
                    }
                    angleToTarget = atan(
                        (listX[target] - listX[id]) /
                                abs(listY[id] - listY[target])
                    )
                    if (listY[target] > listY[id]) angleToTarget += PI
                    mutex.withLock {
                        ser.changeDir(id, angleToTarget)
                    }
                    delay(1000)
                }
            }
        }
    }
}

fun main() = runBlocking{
/*
    GlobalScope.launch {
        delay(1000)
        println("Hello")
    }
    Thread.sleep(2000) // wait for 2 seconds
    println("Stop")
 */
    val ser = Server()
    ser.run()
    for (i in 1..4){
        launch {
            Client().play(ser)
        }
    }
}