package server

import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.*
import io.ktor.util.KtorExperimentalAPI
import io.ktor.utils.io.errors.IOException
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import shared.Config
import shared.Player
import shared.deserialize
import shared.serialize
import java.net.InetSocketAddress

// https://ktor.io/servers/raw-sockets.html

@ObsoleteCoroutinesApi
@KtorExperimentalAPI
fun main() {
    runBlocking {
        val server = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().bind(InetSocketAddress("127.0.0.1", 2323))
        println("Started server at ${server.localAddress}")

        val engineActor = engineActor()

        launch {
            while (true) {
                engineActor.send(Update)
                delay(Config.updateTime)
            }
        }

        while (true) {
            val socket = server.accept()
            launch {
                val input = socket.openReadChannel()
                val output = socket.openWriteChannel(autoFlush = true)

                val registerRequest = CompletableDeferred<Player>()
                engineActor.send(Register(registerRequest))
                val player = registerRequest.await()
                println("${socket.remoteAddress} (player#${player.getId()}) connected")
                val playerEncoded = serialize(player)
                output.writeStringUtf8(playerEncoded + '\n')

                try {
                    while (true) {
                        val line = deserialize(input.readUTF8Line()!!) as ServerRequest

                        when (line)
                        {
                            is GetMapRequest -> {
                                println("Player {${player.getId()}} is requesting: Map")
                                val mapRequest = CompletableDeferred<MutableMap<Int, Player>>()
                                engineActor.send(GetMap(mapRequest))
                                val mapEncoded = serialize(mapRequest.await())
                                output.writeStringUtf8(mapEncoded + '\n')
                            }
                            is SetAngleRequest -> {
                                println("Player {${player.getId()}} is requesting: Angle Set")
                                engineActor.send(SetAngle(line.playerId, line.newAngle))
                            }
                            is GetNewTargetRequest -> {
                                println("Player {${player.getId()}} is requesting: New Target")
                                val getNewTargetRequest = CompletableDeferred<Int?>()
                                engineActor.send(GetNewTarget(line.playerId, getNewTargetRequest))
                                val result = getNewTargetRequest.await()
                                val targetEncoded = serialize(result)
                                output.writeStringUtf8(targetEncoded + '\n')
                            }
                        }
                    }
                } catch (e: IOException) {
                    println("${socket.remoteAddress} (player#${player.getId()}) disconnected")
                    engineActor.send(Remove(player.getId()!!))
                    socket.close()
                }
            }
        }
    }
}