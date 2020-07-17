import kotlin.math.cos
import kotlin.math.sin

class BorovPlayer(val uuid: String) {
    var x = 0.0
    var y = 0.0
    var dir = 0.0
    var vel = 10.0
    val hitradius = 10.0

    var target: String? = null

    fun tick() {
        x += vel * cos(dir)
        y += vel * sin(dir)
    }

    fun debug() {
        println("$uuid ($x $y) > $target");
    }
}