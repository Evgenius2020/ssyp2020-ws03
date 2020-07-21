package engine

import engine.managers.DamageManager
import engine.managers.PositionsManager
import engine.managers.TimersManager
import shared.Bullet
import shared.Entity
import shared.Player


class Engine {
    private val positionsManager = PositionsManager()
    private val timersManager = TimersManager()
    private val damageManager = DamageManager()
    private val listOfPlayers = mutableMapOf<Int, Player>()

    fun registerPlayer(nick: String): Player {
        val player = Player(nick, Configuration.healthOfPlayer)
//        player.team = teamManager.teamChooser(player)
        listOfPlayers[player.id] = player
        positionsManager.register(player)
        timersManager.register(player)
        damageManager.register(player, player.team)
        return player
    }

    fun removePlayer(player: Player) {
        listOfPlayers.remove(player.id)
        positionsManager.removeEntity(player)
        timersManager.removePlayer(player)
    }

    fun tick(){
        val deads = damageManager.processCollisions(positionsManager.moveAll()?.toTypedArray())
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
        listOfPlayers[entity.id]!!.angle = angle
    }

    fun shot(player: Player) {
        // Creates bullet (based on cooldown)
        if (timersManager.checkCooldownTimer(player)){
            val bullet = Bullet(player.team)
            bullet.x = player.x
            bullet.y = player.y
            bullet.angle = player.angle
            damageManager.register(bullet, bullet.team)
            positionsManager.register(bullet)
        }
    }

    fun setFriendlyFire(ff: Boolean?){
        damageManager.friendlyFire = ff
    }
}