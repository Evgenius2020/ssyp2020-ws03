package client

import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.*
import io.ktor.util.KtorExperimentalAPI
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import server.GetMapRequest
import server.GetNewTargetRequest
import server.SetAngleRequest
import shared.Player
import shared.deserialize
import shared.serialize
import java.net.InetSocketAddress

@KtorExperimentalAPI
fun main() {
    runBlocking {
        val socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("127.0.0.1", 2323))
        val input = socket.openReadChannel()
        val output = socket.openWriteChannel(autoFlush = true)

        val player = deserialize(input.readUTF8Line()!!) as Player
        println("Registered: $player")

        while (true) {
            val request = readLine()
            when (request)
            {
                "GetMap" -> {
                    val GetMap = GetMapRequest()
                    output.writeStringUtf8(serialize(GetMap) + '\n')
                    val response = input.readUTF8Line()!!
                    val map = deserialize(response) as MutableMap<Int, Player>
                    println("Player list:")
                    for (i in map.values)
                    {
                        println("Player {${i.getId()}} position is: [${i.getX()}, ${i.getY()}]; target is: [${i.getTargetId()}]; angle is: [{${i.getAngle()}}]")
                    }
                }
                "SetAngle" -> {
                    print("Write new angle (in rads): ")
                    val newAngle = readLine()!!.toDouble()
                    val SetAngle = SetAngleRequest(player.getId()!!, newAngle)
                    output.writeStringUtf8(serialize(SetAngle) + '\n')
                }
                "GetNewTarget" -> {
                    val GetNewTarget = GetNewTargetRequest(player.getId()!!)
                    output.writeStringUtf8(serialize(GetNewTarget) + '\n')
                    val response = input.readUTF8Line()!!
                    val newTarget =  deserialize(response) as Int?
                    println("New target is: $newTarget")
                    player.setTarget(newTarget)
                }
            }
        }
    }
}