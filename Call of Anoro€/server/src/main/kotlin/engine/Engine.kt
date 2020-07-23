package engine

import engine.managers.*
import org.mapeditor.core.TileLayer
import org.mapeditor.io.TMXMapReader
import shared.BOOM
import shared.Bullet
import shared.Entity
import shared.Object
import shared.Player
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random


class Engine {
    private val deadPlayers = mutableListOf<Player>()
    private val positionsManager = PositionsManager()
    private val timersManager = TimersManager()
    private val damageManager = DamageManager()
    private val teamsManager = TeamsManager()
    private val visibilityManager = VisibilityManager()
    private val listOfPlayers = mutableMapOf<Int, Player>()

    val map = TMXMapReader().readMap("../shared/src/jvmMain/resources/map.tmx")

    init {
        for (i in map.layers.indices) {
            if (map.layers[i].name == "solid") {
                for (x in 0 until map.width) {
                    for (y in 0 until map.height) {
                        if ((map.layers[i] as TileLayer).getTileAt(x, y) != null) {
                            registerEntity((x * 32).toDouble() + 16.0, (y * 32).toDouble() + 16.0)
                            println("Position of entity: {${(x * 32).toDouble() + 16.0}, ${(y * 32).toDouble() + 16.0}}")
                        }
                    }
                }
            }
        }
    }

    fun registerPlayer(nick: String): Player {
        val player = Player(nick, Configuration.healthOfPlayer)
        player.x = 0.0
        player.y = 0.0
        listOfPlayers[player.id] = player
        positionsManager.register(player)
        timersManager.register(player)
        damageManager.register(player, player.team)
        teamsManager.register(player)
        visibilityManager.register(player)
        println("player: ${player.x} ${player.y}")
        return player
    }

    fun registerEntity(x: Double, y: Double) {
        val entity = Object()
        entity.x = x
        entity.y = y
        positionsManager.register(entity)
        visibilityManager.register(entity)
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
        visibilityManager.remove(player)
    }

    fun tick() {
        val deds = damageManager.processCollisions(positionsManager.moveAll()?.toTypedArray())
        if (deds != null) {
            for (player in deds) {
                deadPlayers.add(player)
                player.isDead = 1
                timersManager.haveDead(player)
            }
        }
        for (ent in positionsManager.getEntities()) {
            if ((ent is BOOM) && (timersManager.checkBoomTimer(ent))) {
                positionsManager.removeEntity(ent)
                timersManager.remove(ent)
            }
        }
        timersManager.tick()
        for (player in deadPlayers) {
            if (timersManager.checkRespawn(player)) {
                player.x = Random.nextDouble(Configuration.radiusOfPlayer,
                        Configuration.width - Configuration.radiusOfPlayer)
                player.y = Random.nextDouble(Configuration.radiusOfPlayer,
                        Configuration.height - Configuration.radiusOfPlayer)
                player.oldX = 0.0
                player.oldY = 0.0
                player.isDead = 0
                player.health = Configuration.healthOfPlayer
            }
        }
        for (player in listOfPlayers.values) {
            if (player.x == Configuration.radiusOfPlayer && player.y == Configuration.radiusOfPlayer) {
                player.x = Random.nextDouble(Configuration.radiusOfPlayer,
                        Configuration.width - Configuration.radiusOfPlayer)
                player.y = Random.nextDouble(Configuration.radiusOfPlayer,
                        Configuration.height - Configuration.radiusOfPlayer)
            }
            if (player.isDead == 0 && player in deadPlayers) deadPlayers.remove(player)
        }
    }

    fun getEntities(player: Player): Array<Entity> {
        // All visible entities (based on VisibilityManager)
        return visibilityManager.visible(player)
    }

    fun setAngle(entity: Entity, angle: Double) {
        listOfPlayers[entity.id]!!.angle = angle
    }

    fun shot(player: Player) {
        // Creates bullet (based on cooldown)
        if (timersManager.checkCooldownTimer(player)) {
            val bullet = Bullet(player.team, player.angle)
            bullet.x = player.x + (Configuration.radiusOfBullet +
                    Configuration.radiusOfPlayer + 1e-6) * cos(player.angle)
            bullet.y = player.y + (Configuration.radiusOfBullet +
                    Configuration.radiusOfPlayer + 1e-6) * sin(player.angle)
            positionsManager.register(bullet)
            timersManager.haveShooted(player)
            damageManager.register(bullet, bullet.team)
            visibilityManager.register(bullet)
        }
    }

    fun setFriendlyFire(ff: Boolean?) {
        damageManager.friendlyFire = ff
    }

    fun getShootCooldown(player: Player): Double {
        return timersManager.getShootCooldown(player)
    }

    fun getEndGameTime(): Int {
        return timersManager.getGameTimer()
    }
}
