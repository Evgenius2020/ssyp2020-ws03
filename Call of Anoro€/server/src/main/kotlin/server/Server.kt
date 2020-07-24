package server

import Statistic
import engine.Configuration
import engine.Engine
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.ServerSocket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.util.KtorExperimentalAPI
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.readUTF8Line
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import shared.*
import java.net.InetSocketAddress

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
fun CoroutineScope.serverActor() = actor<ServerMsg> {
    val s = ServerActions()

    for (msg in channel) {
        if (!isClosedForReceive) {
            when (msg) {
                is Register -> s.register(msg.u, msg.nick)
                is Tick -> s.tick()
                is GetRenderInfo -> s.getRenderInfo(msg.p, msg.res)
                is SetAngle -> s.setAngle(msg.e, msg.point)
                is Shoot -> s.shoot(msg.p)
                is Disconnect -> s.disconnect(msg.p)
                is ChangeSpeed -> s.changeSpeed(msg.m, msg.x, msg.y)
                is GetStatistic -> s.getStatistic(msg.statistic)
            }
        }
    }
}

class ServerActions {

    val eng = Engine()
    val imageManager = ImageManager()

    init {
        eng.setFriendlyFire(false)
    }

    fun register(res: CompletableDeferred<Player>, nick: String){
        res.complete(eng.registerPlayer(nick))
    }

    fun tick(){
        eng.tick()
    }

    fun getRenderInfo(p: Player, res: CompletableDeferred<RenderInfo>){
        val entities = eng.getEntities(p)
        for(ent in entities){
            if(ent is Player){
                imageManager.setImage(ent.team)
            }
        }
        val cooldown = eng.getShootCooldown(p)
        val respawn = eng.getRespawnTimer(p) / Configuration.fps
        val endGame = eng.getEndGameTime() / Configuration.fps
        val stopTimer = eng.getStopTimer() / Configuration.fps
        res.complete(RenderInfo(entities, imageManager.base, cooldown, endGame, stopTimer, respawn, p.isDead, p.id))
    }

    fun setAngle(e: Entity, point: ClientServerPoint) {
        eng.setAngle(e, kotlin.math.atan2(point.y - e.y, point.x - e.x))
    }

    fun shoot(p: Player) {
        eng.shot(p)
    }

    fun disconnect(p: Player) {
        eng.removePlayer(p)
    }

    fun changeSpeed(m: Moveable, x: Int, y: Int) {
        if(x < -1 || x > 1){
            throw Exception("Cheater")
        }
        if(y < -1 || y > 1){
            throw Exception("Cheater")
        }
        m.speedX = x * Configuration.speedOfPlayer
        m.speedY = y * Configuration.speedOfPlayer
    }

    fun getStatistic(statistic: CompletableDeferred<Statistic>) {
        val teamMembers = hashMapOf<Int, Array<String>>()
        val teamScore = hashMapOf<Int, Int>()
        val nickToKills = hashMapOf<String, Int>()
        val nickToDeaths = hashMapOf<String, Int>()

        for(team in 0 until Configuration.teamCount){
            teamMembers[team] = eng.teamsManager.getNames(team)
        }
        for(team in 0 until Configuration.teamCount){
            teamScore[team] = eng.teamsManager.getScore(team).toInt()
        }
        for(p in eng.positionsManager.getEntities()){
            if(p is Player){
                nickToDeaths[p.nick] = p.deaths
                nickToKills[p.nick] = p.kills
            }
        }
        val stat = Statistic(teamMembers, teamScore, nickToKills, nickToDeaths)
        statistic.complete(stat)
    }
}

class Server {
    val addr = "127.0.0.1"
    val port = 1221
    lateinit var serverSocket: ServerSocket
    lateinit var serverActor: SendChannel<ServerMsg>


    @KtorExperimentalAPI
    fun start() {
        serverSocket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().bind(InetSocketAddress(addr, port))
    }

    @ExperimentalCoroutinesApi
    @ObsoleteCoroutinesApi
    fun run(context: CoroutineScope) {
        runActor(context)
        runUpdater(context)
        runReceiver(context)
    }


    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    private fun runActor(context: CoroutineScope) {
        context.launch {
            serverActor = serverActor()
            println("ActorStarted")
        }
    }

    private fun runUpdater(context: CoroutineScope) {
        context.launch {
            while (true) {
                delay(timeMillis = (1000 / Configuration.fps).toLong())
                serverActor.send(Tick)
            }
        }
    }

    private fun runReceiver(context: CoroutineScope) {
        context.launch {
            while (true) {
                val socket = serverSocket.accept()

                launch {
                    val input = socket.openReadChannel()
                    val output = socket.openWriteChannel(autoFlush = true)

                    val registerMSG = deserialize(input.readUTF8Line()!!)
                    if(registerMSG !is shared.Register){
                        throw Exception("First request must be shared.Register")
                    }

                    val futurePlayer = CompletableDeferred<Player>()
                    serverActor.send(Register(futurePlayer, registerMSG.nick))
                    val p = futurePlayer.await()

                    while (true) {
                        try {
                            communicate(input, output, p)
                        } catch (exc: Exception) {
                            serverActor.send(Disconnect(p))
                            println("Disconnected")
                            break
                        }
                    }
                }
            }
        }
    }

    private suspend fun communicate(input: ByteReadChannel, output: ByteWriteChannel, p: Player) {
        when (val message = deserialize(input.readUTF8Line()!!)) {
            is shared.GetRenderInfo -> {
                val res = CompletableDeferred<RenderInfo>()
                serverActor.send(GetRenderInfo(p, res))
                output.writeStringUtf8(serialize(res.await()) + '\n')
            }
            is shared.SetAngle -> serverActor.send(SetAngle(p, message.point))
            is shared.Shoot -> {
                serverActor.send(Shoot(p))
            }
            is shared.ChangeSpeed -> serverActor.send(ChangeSpeed(p, message.x, message.y))
            is shared.GetStatistic -> {
                val futureStat = CompletableDeferred<Statistic>()
                serverActor.send(GetStatistic(futureStat))
                output.writeStringUtf8(serialize(futureStat.await()) + '\n')
            }
        }
    }

}