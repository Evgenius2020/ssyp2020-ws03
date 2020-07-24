package server

import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.*

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
@KtorExperimentalAPI
fun main() {
    val s = Server()
    s.start()
    runBlocking {
        launch {
            s.run(this)
        }
    }
}