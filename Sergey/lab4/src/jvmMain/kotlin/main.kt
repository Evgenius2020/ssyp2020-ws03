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
class AddPl(val response: CompletableDeferred<Engine.Player>) : ActorMsg()
class GetTar(val playerId: Int, val response: CompletableDeferred<Int?>) : ActorMsg()
class GetInf(val response: CompletableDeferred<MutableList<Point>>) : ActorMsg()
class UpdPl(val playerId: Int, val mul: Double) : ActorMsg()
class CD(val playerId: Int, val angleToTar: Double) : ActorMsg()
class GetInfo(val playerId: Int, val response: CompletableDeferred<Point>) : ActorMsg()
class Run(val pls: String) : ActorMsg()

@ObsoleteCoroutinesApi
fun CoroutineScope.server() = actor<ActorMsg> {
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
            if ((count <= eng.numOfIds) && (i != id)) {
                if (eng.players[i].isTarget != 0) {
                    pl = eng.findPl(i)
                }
            }
            i++
        }
        return when {
            pl != null -> {
                eng.players[i].isTarget = 1
                eng.players[id].isTarget = i - 1
                i
            }
            else -> null
        }
    }
    for (msg in channel) {
        println("Server: Got meaasge $msg")
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
            is UpdPl -> {
                if (msg.mul < 0.0) eng.players[msg.playerId].isTarget = 0
                else eng.players[msg.playerId].haveTarget = null
                eng.updatePlayerState(msg.playerId, msg.mul)
            }
            is CD -> {
                eng.changeDir(msg.playerId, msg.angleToTar)
            }
            is GetInfo -> {
                msg.response.complete(eng.players[msg.playerId].point)
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
                if (sqrt((list[id].x - list[player.haveTarget!!].x).pow(2.0) +
                                (list[id].y - list[player.haveTarget!!].y).pow(2.0)) < radiusc) {

                    ser.send(UpdPl(player.haveTarget!!, -1.0))
                    ser.send(UpdPl(id, 1.0))

                    player.haveTarget = null
                    while (player.haveTarget == null) {
                        delay(1000)
                        val response = CompletableDeferred<Int?>()
                        ser.send(GetTar(player.id, response))
                        player.haveTarget = response.await()
                    }
                }
                angleToTarget = atan(
                        (list[player.haveTarget!!].x - list[id].x) /
                                abs(list[id].y - list[player.haveTarget!!].y)
                )
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
    var ls = mutableListOf<Point>()
    val ser = server()
    withContext(Dispatchers.Default) {
        coroutineScope {
            launch {
                while (true) {
                    delay(100)
                    ser.send(Run("pls"))
                }
            }
            for (i in 0..3) {
                launch {
                    Client().play(ser)
                }

                println("Client $i received state")
                val circle = circle(radius = radiusc, color = Colors.GREEN).xy(-100, -100)
                circle.addUpdater {
                    launch {
                        val response = CompletableDeferred<Point>()
                        ser.send(GetInfo(i, response))
                        val pnt = response.await()
                        x = pnt.x
                        y = pnt.y
                    }
                }

            }
        }
    }
    ser.close()
}