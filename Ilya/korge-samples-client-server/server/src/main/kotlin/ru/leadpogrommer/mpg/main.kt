package ru.leadpogrommer.mpg

import com.soywiz.korim.color.Colors
import io.ktor.util.KtorExperimentalAPI
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream


@KtorExperimentalAPI
fun main() {
//    val buf = ByteArrayOutputStream()
//    val ois = ObjectOutputStream(buf)
//    ois.writeObject(StateRequest(mutableMapOf<Long, Player>(0L to Player())))
//
//    return
    val srv = Server()
    println("Starting server . . .")
    srv.run()
}
