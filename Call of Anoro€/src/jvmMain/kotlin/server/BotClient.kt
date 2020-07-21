package server

import com.soywiz.korio.async.launch
import shared.Entity
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import shared.ClientServerPoint
import kotlin.random.Random

class BotClient(val server: SendChannel<ServerMsg>) {
    suspend fun start() {
        val responsePlayer = CompletableDeferred<Entity>()
        server.send(Register(responsePlayer))
        val e = responsePlayer.await()

        //Не страшно, честно; страшно, честно
        runBlocking {
            launch {
                while (true) {
                    delay(100)
                    server.send(Shoot(e))
                }
            }
            launch {
                while (true) {
                    delay(200)
                    server.send(SetAngle(e, ClientServerPoint(Random.nextDouble(-640.0, 640.0), Random.nextDouble(-640.0, 640.0))))
                }
            }
        }
    }
}