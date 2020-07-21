package ru.leadpogrommer.mpg

import com.soywiz.korim.color.RGBA
import com.soywiz.korma.geom.Point
import java.io.Serializable


sealed class Request: Serializable {
    var __type__ = ""
}
class LoginRequest(val id: Long): Request()
class StateRequest(val state: Map<Long, Player>): Request()
//class ColorRequest(val color: RGBA): Request()
class SetVelocityRequest(val vel: Point): Request()
class DeletePlayerRequest(val id: Long): Request()

