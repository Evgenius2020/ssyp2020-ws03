package engine.managers

import com.soywiz.klock.min
import engine.Configuration
import shared.Entity
import kotlin.math.*
import kotlin.random.Random

data class PositionsManagerData(
        val hitbox: Double = Configuration.radiusOfPlayer
)

class PositionsManager : BaseManager<PositionsManagerData>() {
    fun register(entity: Entity) {
        entity.x = Random.nextDouble(640.0)
        entity.y = Random.nextDouble(640.0)
        super.register(entity, PositionsManagerData())
    }

    fun removeEntity(entity: Entity){
        super.delete(entity)
    }

    fun checkBorders(e: Entity){
        //TODO: REWRITE THIS
        e.x = min(e.x, Configuration.maxx)
        e.y = min(e.y, Configuration.maxy)

        e.x = max(e.x, Configuration.minx)
        e.y = max(e.y, Configuration.miny)
    }

    fun moveAll(): List<Pair<Entity, Entity>> {
        val isChecked = mutableListOf<Entity>()
        val listOfCol = mutableListOf<Pair<Entity, Entity>>()
        for ((entity, posData) in entitiesData) {
            entity.x += Configuration.speedOfPlayer * cos(entity.angle)
            entity.y += Configuration.speedOfPlayer * sin(entity.angle)
            checkBorders(entity)
            isChecked.add(entity)
            for ((entity1, posdata1) in entitiesData) {
                if (entity1 !in isChecked && ((sqrt((entity.x - entity1.x).pow(2.0) +
                                (entity.y - entity1.y).pow(2.0)) < posData.hitbox))) {
                    listOfCol.add(Pair(entity, entity1))
                }
            }
        }
        return listOfCol
    }

    fun getPositions(): Array<Entity> = entitiesData.keys.toTypedArray()
}