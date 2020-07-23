package engine.managers

import engine.Configuration
import shared.*
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

    private fun checkCol(
            obj: Entity,
            entity: Entity,
            isChecked: MutableList<Entity>,
            posData: PositionsManagerData
    ): List<Entity> {
        val re = posData.hitboxes[listOfTypes[entity.id]!!]
        val ro = posData.hitboxes[listOfTypes[obj.id]!!]
        var nocorn = true
        val toRemove = mutableListOf<Entity>()
        if (obj is Object && entity !is Object && entity !in isChecked) {
            var dist = 0.0
            if (entity.y > obj.y + ro && entity.x > obj.x + ro) {
                nocorn = false
                dist = hypot(obj.x - entity.x + ro,
                        obj.y - entity.y + ro)
            }
            if (entity.y > obj.y + ro && entity.x < obj.x - ro) {
                nocorn = false
                dist = hypot(obj.x - entity.x - ro,
                        obj.y - entity.y + ro)
            }
            if (entity.y < obj.y - ro && entity.x > obj.x + ro) {
                nocorn = false
                dist = hypot(obj.x - entity.x + ro,
                        obj.y - entity.y - ro)
            }
            if (entity.y < obj.y - ro && entity.x < obj.x - ro) {
                nocorn = false
                dist = hypot(obj.x - entity.x - ro,
                        obj.y - entity.y - ro)
            }
            if (dist < re && !nocorn) {
                nocorn = false
                if (entity is Bullet) toRemove.add(entity)
                if (entity is Player) {
                    entity.x = entity.oldX
                    entity.y = entity.oldY
                }
            }
            if (nocorn) {
                if ((entity.x - re < obj.x + ro && entity.x + re > obj.x - ro && entity.y < obj.y + ro &&
                                entity.y > obj.y - ro) ||
                        (entity.y - re < obj.x + ro && entity.y + re > obj.y - ro && entity.x < obj.x + ro &&
                                entity.x > obj.x - ro)) {
                    if (entity is Bullet) toRemove.add(entity)
                    if (entity is Player) {
                        entity.x = entity.oldX
                        entity.y = entity.oldY
                    }
                }
            }
        }
        return toRemove
    }

    fun moveAll(): List<Pair<Entity, Entity>>? {
        val isChecked = mutableListOf<Entity>()
        val listOfCol = mutableListOf<Pair<Entity, Entity>>()
        val toRemove = mutableListOf<Entity>()
        for ((entity, posData) in entitiesData) {
            if(entity is Moveable){
                println("Entity pos: ${entity.x} ${entity.y}")
                if (entity is Player){
                    entity.oldX = entity.x
                    entity.oldY = entity.y
                }
                entity.x += entity.speedX
                entity.y += entity.speedY
            }
            if (entity !is Object) {
                checkBorders(entity)
            }
            isChecked.add(entity)
            for (entity1 in entitiesData.keys) {
                val dist = hypot((entity.x - entity1.x), (entity.y - entity1.y))
                if (!(entity is Object || entity1 is Object)) {
                    if (entity1 !in isChecked && ((dist < posData.hitboxes[listOfTypes[entity.id]!!]) || (
                                    dist < posData.hitboxes[listOfTypes[entity1.id]!!])) && (entity !is Bullet ||
                                    entity1 !is Bullet)) {
                        when {
                            entity1 is Player && entity is Player && entity.isDead + entity1.isDead == 0 ->
                                listOfCol.add(Pair(entity, entity1))
                            entity1 is Player && entity1.isDead == 0 && entity is Bullet -> {
                                toRemove.add(entity)
                                listOfCol.add(Pair(entity, entity1))
                            }
                            entity is Player && entity.isDead == 0 && entity1 is Bullet -> {
                                toRemove.add(entity1)
                                listOfCol.add(Pair(entity, entity1))
                            }
                            else -> {
                            }
                        }
                    }
                } else {
                    toRemove.addAll(checkCol(entity, entity1, isChecked, posData))
                    toRemove.addAll(checkCol(entity1, entity, isChecked, posData))
                }
            }
        }
        for (i in toRemove) {
            removeEntity(i)
        }
        return when {
            listOfCol.isEmpty() -> null
            else -> listOfCol
        }
    }

    fun getEntities(): Array<Entity> = entitiesData.keys.toTypedArray()
}