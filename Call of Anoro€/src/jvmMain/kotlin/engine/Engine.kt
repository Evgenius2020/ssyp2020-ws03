package engine

import engine.managers.PositionsManager
import engine.managers.TimersManager
import shared.Entity
import kotlin.concurrent.timer

data class PlayerInfo(
        val pl: Entity,
        val nick: String,
        val health: Int
) {
    val team: Int = -1
}

class Engine {
    private val positionsManager = PositionsManager()
    private val timersManager = TimersManager()
    private val listOfPlayers = mutableMapOf<Int, PlayerInfo>()

    fun registerPlayer(nick: String): Entity {
        val entity = Entity()
        val player = PlayerInfo(entity, nick, Configuration.healthOfPlayer)
//        player.team = teamChooser(player)
        listOfPlayers[entity.id] = player
        positionsManager.register(entity)
        timersManager.register(entity)
        return entity
    }

    fun removePlayer(entity: Entity) {
        listOfPlayers.remove(entity.id)
        positionsManager.removeEntity(entity)
        timersManager.removePlayer(entity)
    }

    fun tick(){
        positionsManager.moveAll()
        timersManager.tick()
    }

    fun getEntities(player: Entity): Array<Entity> {
        // All visible entities (based on VisibilityManager)
        return positionsManager.getPositions()
    }

    fun setAngle(entity: Entity, angle: Double) {
        listOfPlayers[entity.id]!!.pl.angle = angle
    }

    fun shot(entity: Entity) {
        // Creates bullet (based on cooldown)
        println("SHOOT ${entity.id}")
//        if (timersManager.checkCooldownTimer(entity)){

    }

    fun getPlayerInfos(): Array<PlayerInfo> {
        return arrayOf()
    }
}