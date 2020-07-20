package engine.managers

import engine.Configuration
import shared.Entity
import engine.PlayerInfo

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
            entitiesData[ent]!!.cooldown -= 0.016
            entitiesData[ent]!!.respawnTime -= 0.016
            gameTime -= 0.016
        }
    }

    fun checkCooldownTimer(ent: Entity) = when {
        entitiesData[ent]!!.cooldown < 0 -> true
        else -> false
    }

    fun checkRespawn(player: PlayerInfo) = when {
        entitiesData[player.pl]!!.respawnTime < 0 -> true
        else -> false
    }

    fun getGameTimer() = when {
        gameTime < 0.0 -> 0.0
        else -> gameTime
    }

    fun haveShooted(player: PlayerInfo) {
        entitiesData[player.pl]!!.cooldown = Configuration.shootCD
    }

    fun haveDead(player: PlayerInfo) {
        entitiesData[player.pl]!!.respawnTime = Configuration.baseRespawnTime + entitiesData.size.toDouble() *
                ++entitiesData[player.pl]!!.deaths
        if (entitiesData[player.pl]!!.respawnTime > 30.0) entitiesData[player.pl]!!.respawnTime = 30.0
    }
}