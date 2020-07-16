import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.concurrent.thread
import kotlin.math.*

val mutex = Mutex()

class Server {
    private val eng = Engine()
    fun addPl(): Int{
        eng.addPlayer()
        return eng.numOfIds
    }
    fun getTarget(id: Int): Int{
        for (i in eng.players){
            if(i.isTarget == 0 || i.id != id){
                return i.id
            }/*
            if (eng.players.size() == 1){
                return -2
            }*/
        }
        return -1
    }
    fun update() {
        while(true){
            GlobalScope.launch {
                delay(100)
                eng.movePlayers()
            }
        }
    }
    fun getInfX(): MutableList<Double>{
        val a = mutableListOf<Double>()
        for (i in eng.players){
            a.add(i.x)
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
    fun play(ser: Server){
        id = ser.addPl()
        while (target == -1) {
            GlobalScope.launch {
                mutex.withLock {
                    target = ser.getTarget(id)
                }
                if (target == -1) {
                    Thread.sleep(1000)
                }
            }
        }
        while(true){
            GlobalScope.launch {
                mutex.withLock {
                    listX = ser.getInfX()
                    listY = ser.getInfY()
                }
                if (sqrt((listX[id] - listX[target]).pow(2.0) +
                            (listY[id] - listY[target]).pow(2.0)) < 10.0){
                    ser.updPl(id, 0.4)
                    ser.updPl(target, -0.4)
                }
                angleToTarget = atan((listX[target] - listX[id]) /
                        abs(listY[id] - listY[target]))
                if(listY[target] > listY[id]) angleToTarget += PI
                mutex.withLock{
                    ser.changeDir(id, angleToTarget)
                }
                delay(1000)
            }
        }
    }
}

fun main(){
/*
    GlobalScope.launch {
        delay(1000)
        println("Hello")
    }
    Thread.sleep(2000) // wait for 2 seconds
    println("Stop")
 */
    val ser = Server()
    var client1: Client
    var client2: Client
    var client3: Client
    client1.play(ser)
    client2.play(ser)
    client3.play(ser)
}