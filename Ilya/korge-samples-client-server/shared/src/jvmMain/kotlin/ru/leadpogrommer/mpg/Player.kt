package ru.leadpogrommer.mpg

import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.Point
import java.io.Serializable

open class Player: Serializable {
    var id = 0L
    var pos = Point(0, 0)
    var color = Colors.RED
}