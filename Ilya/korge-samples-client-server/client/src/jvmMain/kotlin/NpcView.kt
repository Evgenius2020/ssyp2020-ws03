import com.soywiz.korge.view.Circle
import com.soywiz.korim.color.RGBA
import com.soywiz.korma.geom.Point


class NpcView(r: Double, c: RGBA): Circle(r, c) {
    var vel: Point = Point(0, 0)
}