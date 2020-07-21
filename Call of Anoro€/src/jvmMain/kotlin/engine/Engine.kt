package engine

import engine.managers.DamageManager
import engine.managers.PositionsManager
import engine.managers.TimersManager
import shared.Entity
import kotlin.concurrent.timer

data class PlayerInfo(
        val pl: Entity,
        val nick: String
) {
    val team: Int = -1
}

class Engine {
    private val positionsManager = PositionsManager()
    private val timersManager = TimersManager()
    private val damageManager = DamageManager()
    private val listOfPlayers = mutableMapOf<Int, PlayerInfo>()

    fun registerPlayer(nick: String): Entity {
        val entity = Entity()
        val player = PlayerInfo(entity, nick)
//        player.team = teamManager.teamChooser(player)
        listOfPlayers[entity.id] = player
        positionsManager.register(entity, 0)
        timersManager.register(entity)
        damageManager.register(entity, player.team, false)
        return entity
    }

    fun removePlayer(entity: Entity) {
        listOfPlayers.remove(entity.id)
        positionsManager.removeEntity(entity)
        timersManager.removePlayer(entity)
    }

    fun tick(){
        val deads = damageManager.processCollisions(positionsManager.moveAll())
        if (deads != null){
            for (ents in deads){
                timersManager.haveDead(ents)
            }
        }
        timersManager.tick()
    }

    fun getEntities(player: Entity): Array<Entity> {
        // All visible entities (based on VisibilityManager)
        return positionsManager.getEntities()
    }

    fun setAngle(entity: Entity, angle: Double) {
        listOfPlayers[entity.id]!!.pl.angle = angle
    }

    fun shot(entity: Entity, team: Int) {
        // Creates bullet (based on cooldown)
        if (timersManager.checkCooldownTimer(entity)){
            val bullet = Entity()
            damageManager.register(bullet, team, true)
            positionsManager.register(bullet, 1)
        }
    }
    fun setFriendlyFire(ff: Boolean){
        damageManager.friendlyFire = ff
    }

    fun getPlayerInfos(): Array<PlayerInfo> {
        return arrayOf()
    }
}