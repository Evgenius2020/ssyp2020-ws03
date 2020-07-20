import com.soywiz.klock.seconds
import com.soywiz.korge.*
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*
import com.soywiz.korio.lang.Cancellable
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.degrees
import com.soywiz.korma.interpolation.Easing

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.*
import kotlin.random.Random


sealed class ActorMsg
class AddPl(val response: CompletableDeferred<Engine.Player>): ActorMsg()
class GetTar(val playerId: Int, val response: CompletableDeferred<Int?>): ActorMsg()
class GetInf(val response: CompletableDeferred<MutableList<Point>>): ActorMsg()
class CD(val playerId: Int, val angleToTar: Double): ActorMsg()
class GetStatus(val playerId: Int, val response: CompletableDeferred<Int?>): ActorMsg()
class GetInfo(val playerId: Int, val response: CompletableDeferred<Point?>): ActorMsg()
class Run(val pls: String): ActorMsg()

@ObsoleteCoroutinesApi
fun CoroutineScope.server() = actor<ActorMsg> {
    val eng = Engine()
    fun getTarget(id: Int): Int? {
        if (eng.findPl(id) != null){
            val busyTargets = mutableListOf<Int>()
            for (player in eng.players){
                if (player.isTarget != 0){
                    busyTargets.add(player.id)
                }
            }
            var pl: Engine.Player? = null
            var count = 0
            var i = 0
            while (((i in busyTargets) || (i == id)) && (pl == null)) {
                if (eng.findPl(i) != null) {
                    count++
                }
                i++
                if ((count <= eng.numOfIds) && (i != id) && (eng.findPl(i) != null)) {
                    if (eng.players[i].isTarget == 0) {
                        pl = eng.findPl(i)
                    }
                }
            }
            return when {
                pl != null -> {
                    eng.players[i].isTarget = 1
                    eng.players[id].haveTarget = i
                    i
                }
                i == 0  -> {
                    eng.players[0].isTarget = 1
                    eng.players[id].haveTarget =  0
                    i
                }
                else -> null
            }
        }
        else return null
    }
    for (msg in channel) {
        when (msg) {
            is AddPl -> {
                eng.addPlayer()
                eng.players[eng.numOfIds - 1].haveTarget = getTarget(eng.numOfIds - 1)
                msg.response.complete(eng.players[eng.numOfIds - 1])
            }
            is GetTar -> msg.response.complete(getTarget(msg.playerId))
            is GetInf -> {
                val a = mutableListOf<Point>()
                for (pl in eng.players) {
                    a.add(pl.point)
                }
                msg.response.complete(a)
            }
            is CD -> {
                eng.changeDir(msg.playerId, msg.angleToTar)
            }
            is GetInfo -> {
                if (eng.findPl(msg.playerId) == null){
                    msg.response.complete(null)
                }
                else{
                    msg.response.complete(eng.players[msg.playerId].point)
                }
            }
            is GetStatus -> {
                msg.response.complete(eng.players[msg.playerId].haveTarget)
            }
            is Run -> {
                eng.movePlayers()
            }
        }
    }
}

class Client {
    private var id: Int = -1
    private var list = mutableListOf<Point>()
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
            if (player.haveTarget != null) {
                val response = CompletableDeferred<MutableList<Point>>()
                ser.send(GetInf(response))
                list = response.await()
                val responsible = CompletableDeferred<Int?>()
                ser.send(GetStatus(id, responsible))
                player.haveTarget = responsible.await()
                while (player.haveTarget == null) {
                    val response = CompletableDeferred<Int?>()
                    ser.send(GetTar(player.id, response))
                    player.haveTarget = response.await()
                    delay(500)
                }
                angleToTarget = atan(
                        (list[player.haveTarget!!].x - list[id].x) /
                                (list[id].y - list[player.haveTarget!!].y))
                if (list[player.haveTarget!!].y > list[id].y) angleToTarget += PI
                ser.send(CD(id, angleToTarget))
                delay(1000)
            }
        }
    }
}

@ObsoleteCoroutinesApi
suspend fun main() = Korge(width = width.toInt(), height = height.toInt(),
        bgcolor = Colors["White"]) {
    val ser = server()
    withContext(Dispatchers.Default) {
        coroutineScope {
            launch {
                while (true) {
                    delay(100)
                    ser.send(Run("pls"))
                }
            }
            for (i in 0..10) {
                launch {
                    Client().play(ser)
                }
                var pnt: Point? = null
                val p = (50*i).toDouble()
                val circle = circle(radius = radiusc, color = Colors.GREEN).xy(p, p)
                circle.addUpdater {
                    launch {
                        val response = CompletableDeferred<Point?>()
                        while (pnt == null){
                            ser.send(GetInfo(i, response))
                            pnt = response.await()
                            delay(100)
                        }
                        x = pnt!!.x
                        y = pnt!!.y
                    }
                }
            }
        }
    }
    ser.close()
}