package server

import shared.Entity
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
import shared.ClientServerPoint
import shared.RenderInfo
import shared.deserialize
import shared.serialize
import java.io.IOException
import java.net.InetSocketAddress

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
            println("UPDATER")
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
                        } catch (e: IOException) {

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
                output.writeStringUtf8(serialize(res.await()) + '\n')
            }
            is shared.SetAngle -> serverActor.send(SetAngle(e, message.point))
            is shared.Shoot -> serverActor.send(Shoot(e))
        }
    }

}