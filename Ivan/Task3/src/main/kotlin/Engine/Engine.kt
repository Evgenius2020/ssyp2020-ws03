package Engine

import java.lang.Exception
import java.lang.Math.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.abs

object Engine{
    private val maxX: Double = 1000.0
    private val maxY: Double = 1000.0
    private val speed = 50.0
    private val playerRadius = 1.0 // Player's hitbox is circle
    private val dt = 1.0

    private var players: HashMap<Long, Player> = hashMapOf()

    fun getPlayers(): HashMap<Long, Player> = HashMap(players)

    fun changeDirection(id: Long, dir: Vector) = run { players[id]?.dir = dir.makeLenOne()}

    private fun move(player: Player){
        player.pos.add(player.dir.makeLen(speed * dt))
        player.pos.x = min(maxX, player.pos.x)
        player.pos.y = min(maxY, player.pos.y)
        if(player.pos.x == maxX || player.pos.y == maxY){
            player.dir = Vector(-random(), -random()).makeLenOne()
        }
    }

    private fun teleportRandom(player: Player) = run { player.pos = Dot(maxX * random(), maxY * random()) }

    private fun hit(p: Player){
        teleportRandom(p)
    }

    fun nextState() {
        for(p in players.values){
            if(Vector(0.0, 0.0) != p.dir) {
                move(p)
            }
            if(p.targetId == 0L){
                continue
            }
            if(p.pos.distanceTo(players[p.targetId]!!.pos) < playerRadius){
                hit(p)
            }
        }
    }

    fun addPlayer(): Player{
        val res = Player()
        teleportRandom(res)
        changeDirection(res.id, Vector(maxX * random(), maxY * random()).makeLenOne())
        players[res.id] = res
        return res
    }

    fun clear() = run { players = HashMap() }
}