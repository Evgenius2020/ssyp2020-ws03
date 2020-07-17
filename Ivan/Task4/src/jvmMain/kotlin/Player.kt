data class Player(
    var pos: Dot = Dot(0.0, 0.0),
    var dir: Vector = Vector(0.0, 0.0),
    var targetId: Long = 0,
    var id: Long = IdManager.getFreeId(),
    var busy: Boolean = false
)

object IdManager {
    private var lastId: Long = 1

    fun getFreeId(): Long = lastId++
}