import kotlin.math.PI
import kotlin.random.Random
const val width = 800.0
const val height = 600.0
const val radiusc = 20.0

class Engine {
    var numOfIds: Int = 0
    var players = mutableListOf<Player>()

    fun findPl(id: Int): Player? =
        players.find { player: Player ->
            player.id == id
    }
    data class Player(val id: Int){
        var x: Double = 0.0
        var y: Double = 0.0
        var speedX: Double = 20.0
        var speedY: Double = 0.0
        val speed: Double = 20.0
        var direction: Double = 0.0
        var isTarget = 0
        var haveTarget = -1
        var points = 0.0
    }
    fun addPlayer(x: Double, y: Double) {
        val player = Player(numOfIds++)
        player.x = x
        player.y = y

        players.add(player)
    }
    fun changeDir(id: Int, lr: Double){
        val player = findPl(id)
        if (player != null) {
            player.direction = player.direction
            if(players[id].direction > 2* PI) players[id].direction -= 2 * PI
            if(players[id].direction < 2* PI) players[id].direction += 2 * PI
            player.speedX = (player.speed - player.points*0.4) * kotlin.math.sin(player.direction)
            player.speedY = -(player.speed - player.points*0.4) * kotlin.math.cos(player.direction)
        }
    }
    fun movePlayers(){
        for (player in players){
            if((player.x > width-radiusc) && (player.speedX > 0)){
                player.speedX = -player.speedX
            }
            if((player.x < radiusc) && (player.speedX < 0)){
                player.speedX = -player.speedX
            }
            if((player.y > height-radiusc) && (player.speedY > 0)){
                player.speedY = -player.speedY
            }
            if((player.y < radiusc) && (player.speedY < 0)){
                player.speedY = -player.speedY
            }
            player.x += player.speedX
            player.y += player.speedY
        }
    }
    fun updatePlayerState(id: Int, mul: Double){
        val pl = findPl(id)!!
        pl.x = Random.nextDouble(radiusc, width-radiusc)
        pl.y = Random.nextDouble(radiusc, height-radiusc)
        pl.points += 1.0*mul
        if (mul < 0.0) pl.isTarget = 0
        changeDir(pl.id, Random.nextDouble(2*PI))
    }
}