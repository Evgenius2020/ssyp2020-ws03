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
): Entity()

class Object: Entity()

data class Player(
        val nick: String,
        var health: Int
) : Entity() {
    val team: Int = -1
}
