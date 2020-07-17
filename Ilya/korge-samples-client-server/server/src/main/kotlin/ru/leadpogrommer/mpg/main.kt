package ru.leadpogrommer.mpg

import io.ktor.util.KtorExperimentalAPI


@KtorExperimentalAPI
fun main() {
    val srv = Server()
    println("Starting server . . .")
    srv.run()
}
