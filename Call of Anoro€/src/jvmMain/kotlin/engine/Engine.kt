package engine

import engine.managers.DamageManager
import engine.managers.PositionsManager
import engine.managers.TimersManager
import shared.BOOM
import shared.Bullet
import shared.Entity
import shared.Player
import kotlin.math.cos
import kotlin.math.sin


class Engine {
    private val deadPlayers = mutableListOf<Player>()
    private val positionsManager = PositionsManager()
    private val timersManager = TimersManager()
    private val damageManager = DamageManager()
    private val listOfPlayers = mutableMapOf<Int, Player>()

    //TODO: WRITE TEAM CHOOSER
    private var teamCounter = 0

    fun registerPlayer(nick: String): Player {
        val player = Player(nick, Configuration.healthOfPlayer)
        player.team = teamCounter++
        listOfPlayers[player.id] = player
        positionsManager.register(player)
        timersManager.register(player)
        damageManager.register(player, player.team)
        return player
    }

    fun removePlayer(player: Player) {
        val boom = BOOM()
        boom.x = player.x
        boom.y = player.y
        positionsManager.register(boom)
        timersManager.register(boom)

        println("BOOM")

        listOfPlayers.remove(player.id)
        positionsManager.removeEntity(player)
        timersManager.remove(player)
    }

    fun tick(){
        val deds = damageManager.processCollisions(positionsManager.moveAll()?.toTypedArray())
        if (deds != null){
            for (player in deds){
                deadPlayers.add(player)
                player.isDead = 1
                timersManager.haveDead(player)
            }
        }
        for(ent in positionsManager.getEntities()){
            if((ent is BOOM) && (timersManager.checkBoomTimer(ent))){
                positionsManager.removeEntity(ent)
                timersManager.remove(ent)
            }
        }
        timersManager.tick()
        for (player in deadPlayers){
            if (timersManager.checkRespawn(player)){
                player.isDead = 0
                player.health = Configuration.healthOfPlayer
            }
        }
        for (player in listOfPlayers.values){
            if (player.isDead == 0 && player in deadPlayers) deadPlayers.remove(player)
        }
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