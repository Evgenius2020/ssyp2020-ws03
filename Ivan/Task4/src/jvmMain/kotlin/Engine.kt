import kotlinx.coroutines.sync.Mutex
import kotlin.random.Random

class Engine{
    val playerRadius = 10.0 // Player's hitbox is circle
    val minY = playerRadius
    val minX = 0.0
    val maxX = 640.0 - playerRadius
    val maxY = 480.0
    val speed = 5.0
    val dt = 1.0

    var changed = true

    private var players: HashMap<Long, Player> = hashMapOf()

    fun getPlayers(): HashMap<Long, Player> = players

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

    private fun teleportRandom(player: Player){
        player.pos = Dot(maxX * Random.nextDouble(-1.0, 1.0), maxY * Random.nextDouble(-1.0, 1.0))
    }

    private fun hit(p: Player){
        teleportRandom(p)
        players[p.targetId]!!.busy = false
        p.targetId = 0
    }

    fun nextState() {
        for(p in players.values){
            if(Vector(0.0, 0.0) != p.dir) {
                move(p)
            }
            if(p.targetId != 0L && p.pos.distanceTo(players[p.targetId]!!.pos) < 2 * playerRadius){
                hit(p)
            }
        }
        changed = true
    }

    fun addPlayer(): Player {
        val res = Player()
        teleportRandom(res)
        changeDirection(res.id, Vector(Random.nextDouble(-1.0, 1.0), Random.nextDouble(-1.0, 1.0)).makeLenOne())
        players[res.id] = res
        return res
    }

    fun clear(){
        players = HashMap()
    }
}