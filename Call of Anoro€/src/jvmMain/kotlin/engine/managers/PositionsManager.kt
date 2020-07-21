package engine.managers

import engine.Configuration
import engine.Entity
import engine.PlayerInfo
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class PositionsManagerData(
        val hitboxes: Array<Double> = arrayOf(Configuration.radiusOfPlayer,
        Configuration.radiusOfBullet, Configuration.sizeOfObj),
        val speeds: Array<Double> = arrayOf(Configuration.speedOfPlayer, Configuration.speedOfBullet)
)

class PositionsManager : BaseManager<PositionsManagerData>() {
    private val listOfTypes = mutableMapOf<Int, Int>()

    fun register(entity: Entity, type: Int) {
        super.register(entity, PositionsManagerData())
        listOfTypes[entity.id] = type
    }

    fun removeEntity(entity: Entity){
        listOfTypes.remove(entity.id)
        super.delete(entity)
    }

    fun moveAll(): Array<Pair<Entity, Entity>>? {
        var counter = 0
        val isChecked = mutableListOf<Entity>()
        val listOfCol = arrayOf<Pair<Entity, Entity>>()
        for ((entity, posData) in entitiesData) {
            if (listOfTypes[entity.id]!! < 2){
                entity.x += posData.speeds[listOfTypes[entity.id]!!] * cos(entity.angle)
                entity.y -= posData.speeds[listOfTypes[entity.id]!!] * sin(entity.angle)
            }
            isChecked.add(entity)
            for (entity1 in entitiesData.keys) {
                val dist = sqrt((entity.x - entity1.x).pow(2.0) +
                        (entity.y - entity1.y).pow(2.0))
                if (entity1 !in isChecked && ((dist < posData.hitboxes[listOfTypes[entity.id]!!]) || (
                                dist < posData.hitboxes[listOfTypes[entity1.id]!!]))) {
                    listOfCol[counter++] = (Pair(entity, entity1))
                }
            }
        }
        return when{
            listOfCol.isEmpty() -> null
            else -> listOfCol
        }
    }

    fun getEntities(): Array<Entity> = entitiesData.keys.toTypedArray()
}