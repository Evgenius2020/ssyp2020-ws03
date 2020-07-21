package shared

private var nextId: Int = 1

open class Entity(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var angle: Double = 0.0,
    val id: Int = nextId++
) : java.io.Serializable

data class Bullet(val team: Int): Entity()

class Object: Entity()

data class Player(
        val pl: Entity,
        val nick: String,
        val health: Int
) : Entity() {
    val team: Int = -1
}
