package server

import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.*
import io.ktor.utils.io.errors.IOException
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import shared.Player
import shared.serialize
import java.net.InetSocketAddress

// https://ktor.io/servers/raw-sockets.html

fun main() {
    runBlocking {
        val server = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().bind(InetSocketAddress("127.0.0.1", 2323))
        println("Started server at ${server.localAddress}")

        val engineActor = engineActor()

        launch {
            while (true) {
                engineActor.send(Tick)
                delay(100)
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
                println("${socket.remoteAddress} (player#${player.id}) connected")
                val playerEncoded = serialize(player)
                output.writeStringUtf8(playerEncoded + '\n')

                try {
                    while (true) {
                        val line = input.readUTF8Line()
                        println("${socket.remoteAddress} (player#${player.id}) requested: $line")

                        val playersRequest = CompletableDeferred<List<Player>>()
                        engineActor.send(GetPlayers(playersRequest))
                        val playersEncoded = serialize(playersRequest.await())
                        output.writeStringUtf8(playersEncoded + '\n')
                    }
                } catch (e: IOException) {
                    println("${socket.remoteAddress} (player#${player.id}) disconnected")
                    engineActor.send(Remove(player))
                    socket.close()
                }
            }
        }
    }
}