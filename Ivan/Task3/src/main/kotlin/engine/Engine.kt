package engine

import java.lang.Math.random

object Engine{
    const val minY = 0.0
    const val minX = 0.0
    const val maxX = 1000.0
    const val maxY = 1000.0
    const val speed = 50.0
    const val playerRadius = 10.0 // Player's hitbox is circle
    const val dt = 1.0

    private var players: HashMap<Long, Player> = hashMapOf()

    fun getPlayers(): HashMap<Long, Player> = HashMap(players)

    fun changeDirection(id: Long, dir: Vector){
        players[id]?.dir = dir.makeLenOne()
    }

    private fun checkWalls(player: Player){
        if(player.pos.x < minX){
            player.pos.x = minX
            player.dir.x *= -1
        }
        if(player.pos.x > maxX){
            player.pos.x = maxX
            player.dir.x *= -1
        }
        if(player.pos.y < minY){
            player.pos.y = minY
            player.dir.y *= -1
        }
        if(player.pos.y > maxY){
            player.pos.y = maxY
            player.dir.y *= -1
        }
    }

    private fun move(player: Player){
        player.pos.add(player.dir.makeLen(speed * dt))
        checkWalls(player)
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
            if(p.targetId != 0L && p.pos.distanceTo(players[p.targetId]!!.pos) < playerRadius){
                hit(p)
            }
        }
    }

    fun addPlayer(): Player {
        val res = Player()
        teleportRandom(res)
        changeDirection(res.id, Vector(random(), random()).makeLenOne())
        players[res.id] = res
        return res
    }

    fun clear(){
        players = HashMap()
    }
}