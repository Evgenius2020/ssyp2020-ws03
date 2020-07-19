package client

import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import shared.deserialize
import java.net.InetSocketAddress

fun main() {
    runBlocking {
        val socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("127.0.0.1", 2323))
        val input = socket.openReadChannel()
        val output = socket.openWriteChannel(autoFlush = true)

        val player = deserialize(input.readUTF8Line()!!) as PlayerTest
        println("Registered: $player")

        while (true) {
            output.writeStringUtf8("GetPlayer\n")
            val response = input.readUTF8Line()!!
            val players = deserialize(response) as List<PlayerTest>
            println("Players (${players.size}) list:")
            for (pl in players)
                println("${pl.id} at [${pl.x}, ${pl.y}]")
            delay(1000)
        }
    }
}