package engine.managers

import engine.Configuration
import engine.Entity
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class PositionsManagerData(
        val hitbox: Double = Configuration.radiusOfPlayer
)

class PositionsManager : BaseManager<PositionsManagerData>() {
    fun register(entity: Entity) {
        super.register(entity, PositionsManagerData())
    }

    fun removeEntity(entity: Entity){
        super.delete(entity)
    }

    fun moveAll(): Array<Pair<Entity, Entity>> {
        var counter = 0
        val isChecked = mutableListOf<Entity>()
        val listOfCol = arrayOf<Pair<Entity, Entity>>()
        for ((entity, posData) in entitiesData) {
            entity.x += Configuration.speedOfPlayer * cos(entity.angle)
            entity.y -= Configuration.speedOfPlayer * sin(entity.angle)
            isChecked.add(entity)
            for ((entity1, posdata1) in entitiesData) {
                if (entity1 !in isChecked && ((sqrt((entity.x - entity1.x).pow(2.0) +
                                (entity.y - entity1.y).pow(2.0)) < posData.hitbox))) {
                    listOfCol[counter++] = (Pair(entity, entity1))
                }
            }
        }
        return listOfCol
    }

    fun getPositions(): Array<Entity> = entitiesData.keys.toTypedArray()
}