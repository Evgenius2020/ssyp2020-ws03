package engine.managers

import engine.Entity

data class PositionsManagerData(
    var angle: Double // remove it
    // hitbox
)

class PositionsManager : BaseManager<PositionsManagerData>() {
    fun register(entity: Entity) {
        super.register(entity, PositionsManagerData(0.0))
    }

    fun moveAll() {
        for ((entity, posData) in entitiesData) {
            entity.x += 1
        }
    }

    fun getPositions(): Array<Entity> {
        return arrayOf()
    }
}