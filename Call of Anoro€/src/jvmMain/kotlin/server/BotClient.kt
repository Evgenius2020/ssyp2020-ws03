package server

import com.soywiz.korio.async.launch
import engine.Entity
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import shared.ClientServerPoint
import kotlin.random.Random

class BotClient(val server: SendChannel<ServerMsg>) {
    var pId: Long = 0

    lateinit var me: Entity
    suspend fun start() {
        val responsePlayer = CompletableDeferred<Entity>()
        server.send(Register(responsePlayer))
        val e = responsePlayer.await()

        runBlocking {
            launch {
                while (true) {
                    delay(100)
                    server.send(Shoot(e))
                }
            }
            launch {
                while (true) {
                    delay(50)
                    server.send(SetAngle(e, ClientServerPoint(Random.nextDouble(640.0), Random.nextDouble(480.0))))
                }
            }
        }
    }
}