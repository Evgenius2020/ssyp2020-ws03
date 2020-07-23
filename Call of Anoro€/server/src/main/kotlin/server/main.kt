package server

import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.*

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
@KtorExperimentalAPI
fun main() {
    val s = Server()
    s.start()
    //TODO: он упал
    runBlocking {
        launch {
            s.run(this)
        }

//        launch {
//            delay(2000)
//            repeat(5) {
//                val c = BotClient(s.serverActor)
//                launch {
//                    c.start()
//                }
//            }
//        }
    }
}