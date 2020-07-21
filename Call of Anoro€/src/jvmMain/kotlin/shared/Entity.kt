package shared

import engine.Configuration

private var nextId: Int = 1

open class Entity(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var angle: Double = 0.0,
    val id: Int = nextId++
) : java.io.Serializable

data class Bullet(
        val team: Int,
        val damage: Int = Configuration.baseDamage
): Entity(){
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

class Object: Entity(){
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

data class Player(
        val nick: String,
        var health: Int
) : Entity() {

    var team: Int = -1
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
