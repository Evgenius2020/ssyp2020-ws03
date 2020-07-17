package ru.leadpogrommer.mpg

import com.soywiz.korma.geom.Point

open class Entity {
    var id = 0L
    var pos = Point(0, 0)
    var vel = Point(0, 0)
}