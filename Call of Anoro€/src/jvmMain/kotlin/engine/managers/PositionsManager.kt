package engine.managers

import engine.Configuration
import shared.Bullet
import shared.Entity
import shared.Object
import shared.Player
import kotlin.math.*

data class PositionsManagerData(
        val hitboxes: Array<Double> = arrayOf(Configuration.radiusOfPlayer,
                Configuration.radiusOfBullet, Configuration.sizeOfObj),
        val speeds: Array<Double> = arrayOf(Configuration.speedOfPlayer, Configuration.speedOfBullet)
)

class PositionsManager : BaseManager<PositionsManagerData>() {
    private val listOfTypes = mutableMapOf<Int, Int>()

    fun register(entity: Entity) {
        listOfTypes[entity.id] = when (entity) {
            is Player -> 0
            is Bullet -> 1
            else -> 2
        }
        super.register(entity, PositionsManagerData())
    }

    fun removeEntity(entity: Entity) {
        listOfTypes.remove(entity.id)
        super.delete(entity)
    }

    private fun checkBorders(e: Entity) {
        if (e is Bullet) {
            if ((e.x > Configuration.width + Configuration.radiusOfBullet) ||
                    (e.y > Configuration.height + Configuration.radiusOfBullet) ||
                    (e.x < -Configuration.radiusOfBullet) ||
                    (e.y < -Configuration.radiusOfBullet)) removeEntity(e)
        }
        if (e is Player) {
            e.x = min(e.x, Configuration.width - Configuration.radiusOfPlayer)
            e.y = min(e.y, Configuration.height - Configuration.radiusOfPlayer)

            e.x = max(e.x, Configuration.radiusOfPlayer)
            e.y = max(e.y, Configuration.radiusOfPlayer)
        }
    }

    fun moveAll(): List<Pair<Entity, Entity>>? {
        val isChecked = mutableListOf<Entity>()
        val listOfCol = mutableListOf<Pair<Entity, Entity>>()
        val toRemove = mutableListOf<Entity>()
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
                                dist < posData.hitboxes[listOfTypes[entity1.id]!!])) && (entity !is Bullet ||
                                entity1 !is Bullet)) {
                    listOfCol.add(Pair(entity, entity1))
                    when {
                        entity is Bullet -> toRemove.add(entity)
                        entity1 is Bullet -> toRemove.add(entity1)
                    }
                }
            }
        }
        for (i in toRemove) {
            isChecked.remove(i)
            removeEntity(i)
            toRemove.remove(i)
        }
        return when {
            listOfCol.isEmpty() -> null
            else -> listOfCol
        }
    }

    fun getEntities(): Array<Entity> = entitiesData.keys.toTypedArray()
}