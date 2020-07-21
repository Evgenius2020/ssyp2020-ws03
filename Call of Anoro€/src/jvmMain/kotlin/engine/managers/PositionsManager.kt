package engine.managers

import engine.Configuration
import shared.Entity
import shared.Object
import kotlin.math.*

data class PositionsManagerData(
        val hitboxes: Array<Double> = arrayOf(Configuration.radiusOfPlayer,
                Configuration.radiusOfBullet, Configuration.sizeOfObj),
        val speeds: Array<Double> = arrayOf(Configuration.speedOfPlayer, Configuration.speedOfBullet)
)

class PositionsManager : BaseManager<PositionsManagerData>() {
    private val listOfTypes = mutableMapOf<Int, Int>()

    fun register(entity: Entity) {
        super.register(entity, PositionsManagerData())
//        listOfTypes[entity.id] = type
    }

    fun removeEntity(entity: Entity) {
//        listOfTypes.remove(entity.id)
        super.delete(entity)
    }

    fun checkBorders(e: Entity) {
        e.x = min(e.x, Configuration.width - Configuration.radiusOfBullet)
        e.y = min(e.y, Configuration.height - Configuration.radiusOfBullet)

        e.x = max(e.x, Configuration.radiusOfBullet)
        e.y = max(e.y, Configuration.radiusOfBullet)
    }

    fun moveAll(): List<Pair<Entity, Entity>>? {
        var counter = 0
        val isChecked = mutableListOf<Entity>()
        val listOfCol = mutableListOf<Pair<Entity, Entity>>()
        for ((entity, posData) in entitiesData) {
            if (entity !is Object) {
                entity.x += posData.speeds[listOfTypes[entity.id]!!] * cos(entity.angle)
                entity.y += posData.speeds[listOfTypes[entity.id]!!] * sin(entity.angle)
                checkBorders(entity)
            }
            isChecked.add(entity)
            for (entity1 in entitiesData.keys) {
                val dist = sqrt((entity.x - entity1.x).pow(2.0) +
                        (entity.y - entity1.y).pow(2.0))
                if (entity1 !in isChecked && ((dist < posData.hitboxes[listOfTypes[entity.id]!!]) || (
                                dist < posData.hitboxes[listOfTypes[entity1.id]!!]))) {
                    listOfCol.add(Pair(entity, entity1))
                }
            }
        }
        return when {
            listOfCol.isEmpty() -> null
            else -> listOfCol
        }
    }

    fun getEntities(): Array<Entity> = entitiesData.keys.toTypedArray()
}