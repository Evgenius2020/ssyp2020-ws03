package server

import com.soywiz.korgw.delay
import com.soywiz.korio.async.delay
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
import kotlinx.coroutines.time.delay
import shared.*
import java.net.InetSocketAddress
import kotlin.time.milliseconds
import kotlin.time.seconds

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
fun CoroutineScope.serverActor() = actor<ServerMsg> {
    val s = ServerActions()

    for (msg in channel) {
        if (!isClosedForReceive) {
            when (msg) {
                is Register -> s.register(msg.u)
                is Tick -> s.tick()
                is GetRenderInfo -> s.getRenderInfo(msg.p, msg.res)
                is SetAngle -> s.setAngle(msg.e, msg.point)
                is Shoot -> s.shoot(msg.p)
                is Disconnect -> s.disconnect(msg.p)
                is ChangeSpeed -> s.changeSpeed(msg.m, msg.speedX, msg.speedY)
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

    fun register(res: CompletableDeferred<Player>){
        res.complete(eng.registerPlayer("pepe"))
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
        res.complete(RenderInfo(entities, imageManager.base, cooldown))
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

    fun changeSpeed(m: Moveable, speedX: Double?, speedY: Double?) {
        if(speedX != null){
            m.speedX = speedX
        }
        if(speedY != null){
            m.speedY = speedY
        }
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
                    val futurePlayer = CompletableDeferred<Player>()
                    serverActor.send(Register(futurePlayer))
                    val p = futurePlayer.await()

                    val input = socket.openReadChannel()
                    val output = socket.openWriteChannel(autoFlush = true)

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
            is shared.ChangeSpeed -> serverActor.send(ChangeSpeed(p, message.speedX, message.speedY))
        }
    }

}