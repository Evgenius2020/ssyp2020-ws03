package engine.managers

import engine.Configuration
import shared.Entity
import shared.Player

data class TimersManagerData(
        var cooldown: Double = Configuration.shootCD,
        var respawnTime: Double = Configuration.baseRespawnTime,
        var deaths: Double = 0.0
)

class TimersManager : BaseManager<TimersManagerData>() {
    private var gameTime = Configuration.gameTime

    fun register(entity: Entity) {
        super.register(entity, TimersManagerData())
    }

    fun removePlayer(entity: Entity){
        super.delete(entity)
    }

    fun tick() {
        for ((ent, dat) in entitiesData) {
            entitiesData[ent]!!.cooldown -= Configuration.dt
            entitiesData[ent]!!.respawnTime -= Configuration.dt
            gameTime -= Configuration.dt
        }
    }

    fun checkCooldownTimer(ent: Entity) = when {
        entitiesData[ent]!!.cooldown < 0 -> true
        else -> false
    }

    fun checkRespawn(player: Player) = when {
        entitiesData[player]!!.respawnTime < 0 -> true
        else -> false
    }

    fun getGameTimer() = when {
        gameTime < 0.0 -> 0.0
        else -> gameTime
    }

    fun haveShooted(player: Player) {
        entitiesData[player]!!.cooldown = Configuration.shootCD
    }

    fun haveDead(ent: Entity) {
        entitiesData[ent]!!.respawnTime = Configuration.baseRespawnTime + entitiesData.size.toDouble() *
                ++entitiesData[ent]!!.deaths
        if (entitiesData[ent]!!.respawnTime > 30.0) entitiesData[ent]!!.respawnTime = 30.0
    }
}