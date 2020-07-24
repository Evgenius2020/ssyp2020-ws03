package shared

import engine.Configuration
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private var nextId: Int = 1

open class Entity(
        var x: Double = 0.0,
        var y: Double = 0.0,
        open var angle: Double = 0.0,
        val id: Int = nextId++
) : java.io.Serializable

open class Moveable(var speedX: Double = 0.0,
                    var speedY: Double = 0.0): Entity()

data class Bullet(
        val team: Int,
        override var angle: Double,
        val damage: Int = Configuration.baseDamage,
        val source: Player
): Moveable(speedX = Configuration.speedOfBullet * cos(angle), speedY = Configuration.speedOfBullet * sin(angle)){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Bullet

        if (team != other.team) return false
        if (damage != other.damage) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = team
        result = 31 * result + damage
        result = 31 * result + id
        return result
    }
}

open class Object: Entity(){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Object

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

class BOOM(var started: Boolean = false): Object()

data class Player(
        val nick: String,
        var health: Int
) : Moveable() {
    var isDead = false
    var team = -1
    var oldX = 0.0
    var oldY = 0.0
    var deaths = 0
    var kills = 0
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Player

        if (nick != other.nick) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nick.hashCode()
        result = 31 * result + id
        return result
    }
}

