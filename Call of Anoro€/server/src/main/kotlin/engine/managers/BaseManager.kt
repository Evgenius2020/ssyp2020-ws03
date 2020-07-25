package engine.managers

import shared.Entity

open class BaseManager<DataType> {
    val entitiesData = mutableMapOf<Entity, DataType>()

    protected open fun register(entity: Entity, entityData: DataType) {
        entitiesData[entity] = entityData
    }

    fun delete(entity: Entity) {
        entitiesData.remove(entity)
    }
}