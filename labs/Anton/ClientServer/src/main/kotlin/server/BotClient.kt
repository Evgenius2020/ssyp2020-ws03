package server

import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.*
import io.ktor.util.KtorExperimentalAPI
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import shared.Config
import shared.Player
import shared.deserialize
import shared.serialize
import java.net.InetSocketAddress
import kotlin.math.atan2

class BotClient {
    @KtorExperimentalAPI
    fun run()
    {
        runBlocking {
            val socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("127.0.0.1", 2323))
            val input = socket.openReadChannel()
            val output = socket.openWriteChannel(autoFlush = true)

            val player = deserialize(input.readUTF8Line()!!) as Player
            var targetId = player.getTargetId()
            while (true)
            {
                val mapRequest = GetMapRequest()
                output.writeStringUtf8(serialize(mapRequest) + '\n')
                val response = input.readUTF8Line()!!
                val map = deserialize(response) as MutableMap<Int, Player>
                targetId = map[player.getId()]!!.getTargetId()

                delay(Config.ping)

                if (targetId != null)
                {
                    val tX = map[targetId]!!.getX()
                    val tY = map[targetId]!!.getX()
                    val pX = map[player.getId()!!]!!.getX()
                    val pY = map[player.getId()!!]!!.getY()

                    val angle = atan2(tY - pY, tX - pX)

                    val SetAngle = SetAngleRequest(player.getId()!!, angle)
                    output.writeStringUtf8(serialize(SetAngle) + '\n')
                }
                else
                {
                    val getNewTarget = GetNewTargetRequest(player.getId()!!)
                    output.writeStringUtf8(serialize(getNewTarget) + '\n')
                    val answer = input.readUTF8Line()!!
                    val newTarget = deserialize(answer) as Int?
                    player.setTarget(newTarget)
                }
            }
        }
    }
}