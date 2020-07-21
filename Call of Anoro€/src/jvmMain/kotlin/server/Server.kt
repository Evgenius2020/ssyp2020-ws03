package server

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
                is Register -> s.register(msg.u)
                is Tick -> s.tick()
                is GetRenderInfo -> s.getRenderInfo(msg.e, msg.res)
                is SetAngle -> s.setAngle(msg.e, msg.point)
                is Shoot -> s.shoot(msg.e)
                is Disconnect -> s.disconnect(msg.e)
            }
        }
    }
}

class ServerActions {

    val eng = Engine()

    fun register(res: CompletableDeferred<Entity>){
        res.complete(eng.registerPlayer("pepe"))
    }

    fun tick(){
        eng.tick()
    }

    fun getRenderInfo(e: Entity, res: CompletableDeferred<RenderInfo>){
        res.complete(RenderInfo(eng.getEntities(e), eng.getPlayerInfos()))
    }

    fun setAngle(e: Entity, point: ClientServerPoint) {
        eng.setAngle(e, kotlin.math.atan2(point.y - e.y, point.x - e.x))
    }

    fun shoot(e: Entity) {
        eng.shot(e)
    }

    fun disconnect(e: Entity) {
        eng.removePlayer(e)
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
                delay(16)
                serverActor.send(Tick)
            }
        }
    }

    private fun runReceiver(context: CoroutineScope) {
        context.launch {
            while (true) {
                val socket = serverSocket.accept()

                launch {
                    val futureEntity = CompletableDeferred<Entity>()
                    serverActor.send(Register(futureEntity))
                    val e = futureEntity.await()

                    val input = socket.openReadChannel()
                    val output = socket.openWriteChannel(autoFlush = true)

                    while (true) {
                        try {
                            communicate(input, output, e)
                        } catch (exc: Exception) {
                            serverActor.send(Disconnect(e))
                            println("Disconnected")
                            break
                        }
                    }
                }
            }
        }
    }

    private suspend fun communicate(input: ByteReadChannel, output: ByteWriteChannel, e: Entity) {
        when (val message = deserialize(input.readUTF8Line()!!)) {
            is shared.GetRenderInfo -> {
                val res = CompletableDeferred<RenderInfo>()
                serverActor.send(GetRenderInfo(e, res))
                println("Here")
                output.writeStringUtf8(serialize(res.await()) + '\n')
                println("printed")
            }
            is shared.SetAngle -> serverActor.send(SetAngle(e, message.point))
            is shared.Shoot -> serverActor.send(Shoot(e))
        }
    }

}