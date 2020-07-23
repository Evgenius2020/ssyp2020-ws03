package engine.managers

import engine.Configuration
import shared.BOOM
import shared.Entity
import shared.Player

data class TimersManagerData(
        var cooldown: Double = Configuration.shootCD,
        var respawnTime: Double = 0.0,
        var deaths: Double = 0.0,
        var boomDuration: Double = Configuration.boomDuration
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
                entitiesData[entity]!!.cooldown -= Configuration.dt
            }
            if((entity is BOOM) && (!checkBoomTimer(entity))){
                entitiesData[entity]!!.boomDuration -= Configuration.dt
            }
            if (entity is Player && entitiesData[entity]!!.respawnTime > 0.0)
            entitiesData[entity]!!.respawnTime -= Configuration.dt
            gameTime -= Configuration.dt
        }
    }

    fun getShootCooldown(player: Player): Double {
        return entitiesData[player]!!.cooldown / Configuration.shootCD
    }

    fun checkCooldownTimer(player: Player) = when {
        entitiesData[player]!!.cooldown < 0.0 && player.isDead == 0 -> true
        else -> false
    }

    fun checkBoomTimer(boom: BOOM) = (entitiesData[boom]!!.boomDuration < 0.0)

    fun haveShooted(player: Player) {
        entitiesData[player]!!.cooldown = Configuration.shootCD
    }

    fun checkRespawn(player: Player) = when{
            entitiesData[player]!!.respawnTime < 0 -> true
            else -> false
    }

    fun getGameTimer() = when {
        gameTime < 0.0 -> 0.0
        else -> gameTime
    }

    fun haveDead(player: Player) {
        entitiesData[player]!!.respawnTime = Configuration.baseRespawnTime + entitiesData.size.toDouble() *
                ++entitiesData[player]!!.deaths
        if (entitiesData[player]!!.respawnTime > 30.0) entitiesData[player]!!.respawnTime = 30.0
    }
}