package engine.managers

import engine.Configuration
import shared.Player

data class TimersManagerData(
        var cooldown: Double = Configuration.shootCD,
        var respawnTime: Double = Configuration.baseRespawnTime,
        var deaths: Double = 0.0
)

class TimersManager : BaseManager<TimersManagerData>() {
    private var gameTime = Configuration.gameTime

    fun register(player: Player) {
        super.register(player, TimersManagerData())
    }

    fun removePlayer(player: Player){
        super.delete(player)
    }

    fun tick() {
        for (player in entitiesData.keys) {
            entitiesData[player]!!.cooldown -= Configuration.dt
            entitiesData[player]!!.respawnTime -= Configuration.dt
            gameTime -= Configuration.dt
        }
    }

    fun checkCooldownTimer(player: Player): Boolean {
        return when{
            entitiesData[player]!!.cooldown < 0 ->{
                entitiesData[player]!!.cooldown = Configuration.shootCD
                true
            }
            else -> false
        }
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