package engine.managers

import engine.Configuration
import shared.BOOM
import shared.Entity
import shared.Player
import kotlin.math.min

data class TimersManagerData(
        var cooldown: Int = Configuration.shootCD,
        var respawnTime: Int = 0,
        var deaths: Int = 0,
        var boomDuration: Int = Configuration.boomDuration
)

class TimersManager : BaseManager<TimersManagerData>() {
    private var gameTime = Configuration.gameTime

    fun register(entity: Entity) {
        super.register(entity, TimersManagerData())
    }

    fun remove(entity: Entity){
        super.delete(entity)
    }

    fun tick() {
        for (entity in entitiesData.keys) {
            if ((entity is Player) && (!checkCooldownTimer(entity))){
                entitiesData[entity]!!.cooldown--
            }
            if((entity is BOOM) && (!checkBoomTimer(entity))){
                entitiesData[entity]!!.boomDuration--
            }
            if (entity is Player && entitiesData[entity]!!.respawnTime > 0.0)
            entitiesData[entity]!!.respawnTime--
            gameTime--
        }
    }

    fun getShootCooldown(player: Player): Double {
        return entitiesData[player]!!.cooldown / Configuration.shootCD.toDouble()
    }

    fun checkCooldownTimer(player: Player) = when {
        entitiesData[player]!!.cooldown < 0 && !player.isDead -> true
        else -> false
    }

    fun checkBoomTimer(boom: BOOM) = (entitiesData[boom]!!.boomDuration < 0)

    fun haveShooted(player: Player) {
        entitiesData[player]!!.cooldown = Configuration.shootCD
    }

    fun checkRespawn(player: Player) = (entitiesData[player]!!.respawnTime <= 0)

    fun getGameTimer() = when {
        gameTime < 0.0 -> 0
        else -> gameTime
    }

    fun haveDead(player: Player) {
        entitiesData[player]!!.respawnTime = Configuration.baseRespawnTime + entitiesData.size *
                ++entitiesData[player]!!.deaths
        entitiesData[player]!!.respawnTime = min(30 * Configuration.fps, entitiesData[player]!!.respawnTime)
    }
}