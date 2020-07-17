import com.soywiz.klock.seconds
import com.soywiz.korge.*
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.degrees
import com.soywiz.korma.interpolation.Easing

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.*
import kotlin.random.Random

val mutex = Mutex()

sealed class ActorMsg
class AddPl(val response: CompletableDeferred<Engine.Player>) : ActorMsg()
class GetTar(val playerId: Int, val response: CompletableDeferred<Int?>) : ActorMsg()
class GetInfX(val response: CompletableDeferred<MutableList<Double>>) : ActorMsg()
class GetInfY(val response: CompletableDeferred<MutableList<Double>>) : ActorMsg()
class UpdPl(val playerId: Int, val mul: Double) : ActorMsg()
class CD(val playerId: Int, val angleToTar: Double) : ActorMsg()

fun CoroutineScope.Server() = actor<ActorMsg> {
    val eng = Engine()
    fun getTarget(id: Int): Int? {
        val busyTargets: List<Int> = eng.players.filter { it.isTarget != 0 }.map { it.id }
        var pl: Engine.Player? = null
        var count = 0
        var i = 0
        while (((i in busyTargets) || (i == id)) && (pl == null)) {
            if (eng.findPl(i) != null) {
                count++
            }
            i++
            if ((count <= eng.numOfIds) && (i != id)) {
                if (eng.players[i].isTarget != 0) {
                    pl = eng.findPl(i)
                }
            }
        }
        return when {
            pl != null -> {
                eng.players[i].isTarget = 1
                eng.players[id].isTarget = i
                i
            }
            else -> null
        }
    }
    for (msg in channel) {
        when (msg) {
            is AddPl -> {
                eng.addPlayer()
                eng.players[eng.numOfIds - 1].haveTarget = getTarget(eng.numOfIds - 1)
                msg.response.complete(eng.players[eng.numOfIds - 1])
            }
            is GetTar -> msg.response.complete(getTarget(msg.playerId))
            is GetInfX -> {
                val a = mutableListOf<Double>()
                for (pl in eng.players) {
                    a.add(pl.x)
                }
                msg.response.complete(a)
            }
            is GetInfY -> {
                val a = mutableListOf<Double>()
                for (pl in eng.players) {
                    a.add(pl.y)
                }
                msg.response.complete(a)
            }
            is UpdPl -> {
                if (msg.mul < 0.0) eng.players[msg.playerId].isTarget = 0
                else eng.players[msg.playerId].haveTarget = null
                eng.updatePlayerState(msg.playerId, msg.mul)
            }
            is CD -> {
                eng.changeDir(msg.playerId, msg.angleToTar)
            }
        }
    }
    suspend fun run() {
        while (true) {
            delay(100)
            eng.movePlayers()
        }
    }
}

class Client{
    private var id: Int = -1
    private var listX = mutableListOf<Double>()
    private var listY = mutableListOf<Double>()
    private var angleToTarget = -1.0
    suspend fun play(ser: SendChannel<ActorMsg>) {
        val response = CompletableDeferred<Engine.Player>()
        ser.send(AddPl(response))
        val player = response.await()
        id = player.id
        while (player.haveTarget == null) {
            delay(1000)
            val response = CompletableDeferred<Int?>()
            ser.send(GetTar(player.id, response))
            player.haveTarget = response.await()
        }
        while (true) {
            if (player.haveTarget != null){
                mutex.withLock {
                    val response = CompletableDeferred<MutableList<Double>>()
                    ser.send(GetInfX(response))
                    listX = response.await()
                    //val response = CompletableDeferred<MutableList<Double>>()
                    ser.send(GetInfY(response))
                    listY = response.await()
                }
                if (sqrt((listX[id] - listX[player.haveTarget!!]).pow(2.0) +
                                (listY[id] - listY[player.haveTarget!!]).pow(2.0)) < radiusc)
                {
                    mutex.withLock {
                        ser.send(UpdPl(player.haveTarget!!, -1.0))
                        ser.send(UpdPl(id, 1.0))
                    }
                    player.haveTarget = null
                    while (player.haveTarget == null) {
                        delay(1000)
                        val response = CompletableDeferred<Int?>()
                        ser.send(GetTar(player.id, response))
                        player.haveTarget = response.await()
                    }
                }
                angleToTarget = atan(
                        (listX[player.haveTarget!!] - listX[id]) /
                                abs(listY[id] - listY[player.haveTarget!!])
                )
                if (listY[player.haveTarget!!] > listY[id]) angleToTarget += PI
                mutex.withLock {
                    ser.send(CD(id, angleToTarget))
                }
                delay(1000)
            }
        }
    }

}

suspend fun main() = Korge(width = width.toInt(), height = height.toInt(),
        bgcolor = Colors["White"]) {
    runBlocking {
        val ser = counterActor()
        withContext(Dispatchers.Default) {
            coroutineScope {
                repeat(4) {
                    launch {
                        Client().play(ser)
                    }
                }
            }
        }
        ser.close()
    }
    val listX = mutableListOf<Double>()
    val listY = mutableListOf<Double>()
    val circle = mutableListOf<Circle>()
        listX.add(Random.nextDouble(radiusc, width-radiusc))
        listY.add(Random.nextDouble(radiusc, height-radiusc))
        circle.add(circle(radius = radiusc, color = Colors.GREEN).xy(listX[i], listY[i]))
        Client().play(ser, listX[i], listY[i])
        circle[i].addUpdater(){
            listX[i] = ser.getInfX(i)
            listY[i] = ser.getInfY(i)
    }
}