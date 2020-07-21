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

    fun registerPlayer(nick: String): Entity {
        val entity = Entity()
        val player = Player(entity, nick, Configuration.healthOfPlayer)
//        player.team = teamManager.teamChooser(player)
        listOfPlayers[entity.id] = player
        positionsManager.register(entity)
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
        listOfPlayers[entity.id]!!.pl.angle = angle
    }

    fun shot(player: Player) {
        // Creates bullet (based on cooldown)
        if (timersManager.checkCooldownTimer(player)){
            val bullet = Bullet(player.team)
            damageManager.register(bullet, listOfPlayers[player.id]!!.team, true)
            positionsManager.register(bullet)
        }
    }

    fun setFriendlyFire(ff: Boolean){
        damageManager.friendlyFire = ff
    }
}