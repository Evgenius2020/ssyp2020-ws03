package engine

import engine.managers.DamageManager
import engine.managers.PositionsManager
import engine.managers.TimersManager
import shared.Bullet
import shared.Entity
import shared.Player
import kotlin.math.cos
import kotlin.math.sin


class Engine {
    private val positionsManager = PositionsManager()
    private val timersManager = TimersManager()
    private val damageManager = DamageManager()
    private val listOfPlayers = mutableMapOf<Int, Player>()

    //TODO: WRITE TEAM CHOOSER
    private var teamCounter = 0

    fun registerPlayer(nick: String): Player {
        val player = Player(nick, Configuration.healthOfPlayer)
        player.team = teamCounter++
        println("id: ${player.id}")
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
            for (ent in deads){
                timersManager.haveDead(ent)
                positionsManager.removeEntity(ent)
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
            bullet.x = player.x + (Configuration.radiusOfBullet +
                    Configuration.radiusOfPlayer + 1e-6)* cos(player.angle)
            bullet.y = player.y + (Configuration.radiusOfBullet +
                    Configuration.radiusOfPlayer + 1e-6)* sin(player.angle)
            bullet.angle = player.angle
            positionsManager.register(bullet)
            timersManager.haveShooted(player)
            damageManager.register(bullet, bullet.team)
        }
    }

    fun setFriendlyFire(ff: Boolean?){
        damageManager.friendlyFire = ff
    }
}