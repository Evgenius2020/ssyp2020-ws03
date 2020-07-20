package shared

private var nextId: Int = 1

open class Entity(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var angle: Double = 0.0,
    val id: Int = nextId++
)

