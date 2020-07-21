package ru.leadpogrommer.mpg

import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.Point
import java.io.Serializable

open class Player: Serializable {
    var id = 0L
    var pos = Point(64, 64)
    var color = Colors.RED
    var vel = Point(0, 0)
    val radius = 16
//    val raius =
}