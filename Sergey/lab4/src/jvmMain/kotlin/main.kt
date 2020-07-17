import com.soywiz.korge.Korge
import com.soywiz.korge.view.Circle
import com.soywiz.korge.view.addUpdater
import com.soywiz.korge.view.circle
import com.soywiz.korge.view.xy
import com.soywiz.korim.color.Colors
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.*
import kotlin.random.Random

val mutex = Mutex()

class Server {
    val eng = Engine()
    fun addPl(x: Double, y: Double): Int {
        eng.addPlayer(x, y)
        return eng.numOfIds - 1
    }

    fun getTarget(id: Int): Int {
        var pl: Any
        pl = if (id != 0) {
            eng.findPl(id - 1)!!
        } else {
            eng.findPl(eng.numOfIds - 1)!!
        }
        if ((pl.isTarget == 0) && (pl.id != id) && (pl.haveTarget != id)) {
            pl.isTarget = 1
            val pla = eng.findPl(id)!!
            pla.haveTarget = pl.id
            return pl.id
        }
        return -1
    }

    fun run() {
        GlobalScope.launch {
            while (true) {
                delay(100)
                eng.movePlayers()
            }
        }
    }

    fun getInfX(): MutableList<Double> {
        val a = mutableListOf<Double>()
        for (pl in eng.players) {
            a.add(pl.x)
        }
        return a
    }

    fun getInfY(): MutableList<Double> {
        val a = mutableListOf<Double>()
        for (i in eng.players) {
            a.add(i.y)
        }
        return a
    }

    fun getInfX(id: Int): Double {
        val a = eng.findPl(id)!!
        return a.x
    }

    fun getInfY(id: Int): Double {
        val a = eng.findPl(id)!!
        return a.y
    }

    fun changeDir(id: Int, angleToTar: Double) {
        eng.changeDir(id, angleToTar)
    }

    fun updPl(id: Int, mul: Double) {
        eng.updatePlayerState(id, mul)
    }
}

class Client {
    private var id = -1
    private var target = -1
    private var listX = mutableListOf<Double>()
    private var listY = mutableListOf<Double>()
    private var angleToTarget = -1.0
    suspend fun play(ser: Server, x: Double, y: Double) {
        id = ser.addPl(x, y)
        suspend fun gettar() {
            while (target == -1) {
                mutex.withLock {
                    target = ser.getTarget(id)
                }
                delay(1000)
            }
        }
        gettar()
        while (true) {
            if (target != -1) {
                mutex.withLock {
                    listX = ser.getInfX()
                    listY = ser.getInfY()
                }
                if (sqrt((listX[id] - listX[target]).pow(2.0) +
                                (listY[id] - listY[target]).pow(2.0)
                        ) < radiusc) {
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

suspend fun main() = Korge(width = width.toInt(), height = height.toInt(),
        bgcolor = Colors["White"]) {
    val ser = Server()
    ser.run()
    for (i in 0..3) {
        val cir = circle(radius = radiusc, color = Colors.GREEN).xy(
                Random.nextDouble(radiusc, width - radiusc),
                Random.nextDouble(radiusc, height - radiusc))
        print("1")
        launch {
            Client().play(ser, cir.x, cir.y)
        }
        print("2")
        launch {
            cir.addUpdater {
                x = ser.getInfX(i)
                y = ser.getInfY(i)
            }
        }
        print("3")
    }
}