package engine

import engine.managers.PositionsManager

data class PlayerInfo(
    val pl: Entity,
    val nick: String,
    val health: Int,
    val teamNumber: Int
)

class Engine {
    private val positionsManager = PositionsManager()

    fun registerPlayer(): Entity {
        val entity = Entity()
        positionsManager.register(entity)
        return Entity()
    }

    fun removePlayer(player: Entity) {}

    fun getEntities(player: Entity): Array<Entity> {
        // All visible entities (based on VisibilityManager)
        return arrayOf()
    }

    fun setAngle(entity: Entity, angle: Double) {}

    fun shot(entity: Entity) {
        // Creates bullet (based on cooldown)
    }

    fun getPlayerInfos(): Array<PlayerInfo> {
        return arrayOf()
    }

    fun tick() {}
}