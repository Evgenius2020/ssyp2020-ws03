package engine

data class PlayerInfo(
    val pl: Entity,
    val nick: String,
    val health: Int,
    val teamNumber: Int
)

class Engine {
    fun registerPlayer(): Entity {
        return Entity()
    }

    fun removePlayer(player: Entity) {}

    fun getEntities(player: Entity) {
        // All visible entities (based on VisibilityManager)
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